package com.rj.stomp.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class WebSocketFrameToByteBufCombineHandler extends CombinedChannelDuplexHandler<WebSocketFrameToByteBufDecoder,ByteBufToWebSocketFrameEncoder> {


    public WebSocketFrameToByteBufCombineHandler(WebSocketFrameToByteBufDecoder inboundHandler, ByteBufToWebSocketFrameEncoder outboundHandler) {
        super(inboundHandler, outboundHandler);
    }
}
