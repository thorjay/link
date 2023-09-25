package com.rj.stomp.codec;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.util.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @desc in：将WebSocketFrame转换为ByteBuf
 * @author larryjay
 * @since 2023/9/19 19:15
*/
public class WebSocketFrameToByteBufDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    public static final Log log = LogFactory.get();

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf bb = msg.content();
        int readable = bb.readableBytes();
        if(readable > Constant.WebSocketConfig.MAX_FRAME_SIZE.getValue()){
            TooLongFrameException e = new TooLongFrameException("Frame too long !");
            log.error(e,"{} decode()异常: {}" ,this.getClass().getName(),e.getMessage());
            throw e;
        }
        log.info("{} decode() 成功: {}" ,this.getClass().getName(),bb.toString(CharsetUtil.UTF_8));
        out.add(bb.retain());
    }
}
