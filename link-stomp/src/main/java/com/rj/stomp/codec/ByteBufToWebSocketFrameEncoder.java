package com.rj.stomp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * @desc out ： 将ByteBuf为WebSocketFrame
 * @author larryjay
 * @since 2023/9/19 19:15
*/
public class ByteBufToWebSocketFrameEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        TextWebSocketFrame frame = new TextWebSocketFrame();
        frame.content().writeBytes(msg);
        out.add(frame);
    }
}
