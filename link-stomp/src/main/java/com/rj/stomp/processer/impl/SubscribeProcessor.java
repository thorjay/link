package com.rj.stomp.processer.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.processer.IStompProcessor;
import com.rj.stomp.subcribe.StompSubscribeManager;
import com.rj.stomp.subcribe.StompSubscription;
import com.rj.stomp.util.StompNettyUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

import java.util.HashSet;
import java.util.Set;

import static io.netty.handler.codec.stomp.StompHeaders.ID;
import static io.netty.handler.codec.stomp.StompHeaders.RECEIPT_ID;

public class SubscribeProcessor implements IStompProcessor {

    public static final Log log = LogFactory.get();

    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception{
        String destination = frame.headers().getAsString(StompHeaders.DESTINATION);
        String subId = frame.headers().getAsString(ID);

        if(destination == null || subId == null){
            StompNettyUtil.sendErrorFrame("missed header", "Required 'destination' or 'id' header missed", ctx);
            return;
        }

        /**获取destination下的订阅连接*/
        Set<StompSubscription> subscriptions = StompSubscribeManager.getSubscriptions(destination);
        if(subscriptions == null){
            subscriptions = new HashSet<StompSubscription>();
            Set<StompSubscription> previousSubcriptions = StompSubscribeManager.addSubscription(destination,subscriptions);
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
                StompSubscribeManager.getSubscriptions(subscription.getDestination()).remove(subscription);
            }
        });
        //判断是否有RECEIPT
        String  receiptId = frame.headers().getAsString(StompHeaders.RECEIPT);
        if(receiptId != null){
            StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
            receiptFrame.headers().set(RECEIPT_ID, receiptId);
            ctx.channel().writeAndFlush(receiptFrame);
        }
    }
}
