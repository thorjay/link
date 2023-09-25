package com.rj.stomp.util;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.stomp.StompCommand.ERROR;
import static io.netty.handler.codec.stomp.StompHeaders.MESSAGE;

public class StompNettyUtil {


    /**
     * 发送错误帧
     * @param msg
     * @param description
     * @param ctx
     */
    public static void sendErrorFrame(String msg, String description, ChannelHandlerContext ctx){
        StompFrame errorFrame = new DefaultStompFrame(ERROR);
        errorFrame.headers().set(MESSAGE,msg);
        if(description != null){
            errorFrame.content().writeCharSequence(description, CharsetUtil.UTF_8);
        }
        ctx.writeAndFlush(errorFrame).addListener(ChannelFutureListener.CLOSE);
    }

}
