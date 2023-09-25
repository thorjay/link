package com.rj.stomp.codec;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.initializer.StompWebSocketInitializer;
import com.rj.stomp.util.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class WebSocketToByteBufCodec extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

    private static final Log log = LogFactory.get();

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame();
        textWebSocketFrame.content().writeBytes(msg);
        out.add(textWebSocketFrame);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf bb = msg.content();
        int readable = bb.readableBytes();
        if(readable > Constant.WebSocketConfig.MAX_FRAME_SIZE.getValue()){
            bb.skipBytes(readable);
            Exception e = new TooLongFrameException("Frame too long: " + readable + " > " + Constant.WebSocketConfig.MAX_FRAME_SIZE.getValue());
            log.error(e,"{} decode() 出现异常，异常为{}" ,this.getClass().getName() ,e.getMessage());
            throw e;
        }
        bb.retain();
        out.add(bb);
    }
}
