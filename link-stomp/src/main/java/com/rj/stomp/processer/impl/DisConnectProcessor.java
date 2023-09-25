package com.rj.stomp.processer.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.processer.IStompProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;

import java.util.Optional;

import static io.netty.handler.codec.stomp.StompHeaders.*;

public class DisConnectProcessor implements IStompProcessor {
    public static final Log log = LogFactory.get();

    @Override
    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception {
        log.info("disconnect process + id:{},destination:{}",frame.headers().getAsString(ID),frame.headers().getAsString(DESTINATION));
        String receiptId = frame.headers().getAsString(RECEIPT);
        Optional.ofNullable(receiptId).ifPresentOrElse(id ->{
            StompFrame disConnectFrame = new DefaultStompFrame(StompCommand.DISCONNECT);
            disConnectFrame.headers().add(RECEIPT, id);
            ctx.writeAndFlush(disConnectFrame);
        }, () -> {
            ctx.close();
        });
    }
}
