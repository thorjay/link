package com.rj.stomp.processer.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.processer.IStompProcessor;
import com.rj.stomp.subcribe.StompSubscribeManager;
import com.rj.stomp.subcribe.StompSubscription;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UnSubscribeProcessor implements IStompProcessor {

    public static final Log log = LogFactory.get();

    @Override
    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception {
        String destination = frame.headers().getAsString(StompHeaders.DESTINATION);
        String subscriptionId = frame.headers().getAsString(StompHeaders.SUBSCRIPTION);
//        Set<StompSubscription> subscriptions = StompSubscribeManager.getDestination(destination);
        Map<String, Set<StompSubscription>> destinationMap = StompSubscribeManager.getDestinationMap();
        Set<Map.Entry<String, Set<StompSubscription>>> entries = destinationMap.entrySet();
        for (Map.Entry<String, Set<StompSubscription>> entry : entries) {
            Iterator<StompSubscription> iterator = entry.getValue().iterator();
            while (iterator.hasNext()){
                StompSubscription subscription = iterator.next();
                if(subscription.getId().equals(subscriptionId) && subscription.getChannel().equals(ctx.channel())){
                    iterator.remove();
                    return;
                }
            }
        }
    }

}
