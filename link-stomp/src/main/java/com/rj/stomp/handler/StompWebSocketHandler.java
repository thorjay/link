package com.rj.stomp.handler;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.processer.IStompProcessor;
import com.rj.stomp.subcribe.StompSubscription;
import com.rj.stomp.util.StompNettyUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.util.CharsetUtil;

import javax.swing.text.Utilities;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.netty.handler.codec.stomp.StompCommand.ERROR;
import static io.netty.handler.codec.stomp.StompHeaders.*;

/**
 * @desc 基于websocket的stomp数据进行自定义业务处理
 * @author larryjay
 * @since 2023/9/19 15:05
*/
public class StompWebSocketHandler extends SimpleChannelInboundHandler<StompFrame> {

    private static final Log log = LogFactory.get();


    /**{destination : 订阅集合}*/
    private final ConcurrentMap<String, Set<StompSubscription>> stompDestinations = new ConcurrentHashMap<>();


    /**
     * Stomp帧消息的入口，自定义业务操作，根据不同的Command进入不同的Processor处理
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StompFrame msg) throws Exception {
        log.info("{}.channelRead0 receive message: {}", this.getClass().getName(),msg);
        IStompProcessor iStompProcessor = IStompProcessor.Type.fromCommand(msg.command()).factory.create();
        iStompProcessor.process(ctx,msg);
//        if(stompFrame == null){
//            return;
//        }
//        switch (msg.command()){
//            case DISCONNECT:
//            case ERROR:
//                ctx.writeAndFlush(stompFrame).addListener(ChannelFutureListener.CLOSE);
//                break;
//            case CONNECT:
//            case SEND:
//            case SUBSCRIBE:
//            case UNSUBSCRIBE:
//                ctx.writeAndFlush(stompFrame).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//                break;
//            case MESSAGE:
//            case RECEIPT:
//            case CONNECTED:
//            case BEGIN:
//            case ACK:
//            case NACK:
//            default:
//                ctx.writeAndFlush(stompFrame).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//                break;
//        }
    }


    private void onConnect(ChannelHandlerContext ctx, StompFrame frame){

    }

    private void onSubscribe(ChannelHandlerContext ctx, StompFrame frame){
        String destination = frame.headers().getAsString(StompHeaders.DESTINATION);
        String subId = frame.headers().getAsString(ID);

        if(destination == null || subId == null){
            StompNettyUtil.sendErrorFrame("missed header", "Required 'destination' or 'id' header missed", ctx);
            return;
        }

        /**获取destination下的订阅连接*/
        Set<StompSubscription> subscriptions = stompDestinations.get(destination);
        if(subscriptions == null){
          subscriptions = new HashSet<StompSubscription>();
          Set<StompSubscription> previousSubcriptions = stompDestinations.putIfAbsent(destination,subscriptions);
          if(previousSubcriptions != null ){
              subscriptions = previousSubcriptions;
          }
        }

        final StompSubscription subscription = new StompSubscription(subId,destination,ctx.channel());
        if(subscriptions.contains(subscription)){
            StompNettyUtil.sendErrorFrame("duplicate subscription",
                    "Received duplicate subscription id=" + subId, ctx);
            return;
        }
        subscriptions.add(subscription);
        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                stompDestinations.get(subscription.getDestination()).remove(subscription);
            }
        });
        //判断是否有RECEIPT
        String  receiptId = frame.headers().getAsString(StompHeaders.RECEIPT);
        if(receiptId != null){
            StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
            receiptFrame.headers().set(RECEIPT_ID, receiptId);
            ctx.writeAndFlush(receiptFrame);
        }
    }

    private void onSend(ChannelHandlerContext ctx, StompFrame frame){

    }

    private void onUnsubscribe(ChannelHandlerContext ctx, StompFrame frame){

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        /**清除连接引用的缓存*/

        /**清除请阅消息，重连后需要重新订阅*/
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause,"{} 异常消息 ： {}",StompWebSocketHandler.class.getName(),cause.getMessage());
        StompFrame errorFrame = new DefaultStompFrame(StompCommand.ERROR);
        errorFrame.headers().set(StompHeaders.MESSAGE,cause.getMessage());
        ctx.writeAndFlush(errorFrame).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info( "{} channelActive :" + ctx.channel().remoteAddress() + " 连接成功",StompWebSocketHandler.class.getName());
        super.channelActive(ctx);
    }

}
