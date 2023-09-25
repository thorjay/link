package com.rj.stomp.processer.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.processer.IStompProcessor;
import com.rj.stomp.subcribe.StompSubscribeManager;
import com.rj.stomp.subcribe.StompSubscription;
import com.rj.stomp.util.StompNettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompFrame;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static io.netty.handler.codec.stomp.StompCommand.MESSAGE;
import static io.netty.handler.codec.stomp.StompHeaders.*;

/**
 * @desc 发送消息的处理
 * @author larryjay
 * @since 2023/9/20 17:42
*/
public class SendProcessor implements IStompProcessor {

    public static final Log log = LogFactory.get();

    @Override
    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception {
        String destination = frame.headers().getAsString(DESTINATION);
        String id = frame.headers().getAsString(ID);
        String content_type = frame.headers().getAsString(CONTENT_TYPE);

        log.info("destination: {}, id: {}, content_type: {}", destination, id, content_type);

        if(destination == null){
            StompNettyUtil.sendErrorFrame("missed header", "required 'destination' header missed", ctx);
            return;
        }
        /**通过destination获取channel，来讲消息传给messageChannel*/
        Set<StompSubscription> subscriptions = StompSubscribeManager.getSubscriptions(destination);
        if(subscriptions == null || subscriptions.isEmpty()){
            StompNettyUtil.sendErrorFrame("subscriptions is empty", "subscriptions is empty", ctx);
            return;
        }
        /**将消息发给stompSubcriber*/
        for (StompSubscription subscription : subscriptions) {
            StompFrame messageFrame = transFrame(frame,subscription);
            subscription.getChannel().writeAndFlush(messageFrame);
        }
    }

    private StompFrame transFrame(StompFrame sendFrame, StompSubscription subscription){
        StompFrame messageFrame = new DefaultStompFrame(MESSAGE,sendFrame.content().retainedDuplicate());
        String id = UUID.randomUUID().toString();
        messageFrame.headers().set(MESSAGE_ID,id)
                .set(SUBSCRIPTION,subscription.getId())
                .set(CONTENT_LENGTH,Integer.toString(sendFrame.content().readableBytes()));
        CharSequence contentType = sendFrame.headers().get(CONTENT_TYPE);
        Optional.ofNullable(contentType).ifPresent(t -> messageFrame.headers().set(t));
        log.info("send message to {},message is {}",subscription.getId(),messageFrame.toString());
        return messageFrame;
    }

}
