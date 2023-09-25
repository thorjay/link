package com.rj.stomp.initializer;

import com.rj.netty.handler.CommonChannelIdleTimeoutHandler;
import com.rj.stomp.codec.ByteBufToWebSocketFrameEncoder;
import com.rj.stomp.codec.WebSocketFrameToByteBufDecoder;
import com.rj.stomp.handler.StompWebSocketHandler;
import com.rj.stomp.util.Constant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.stomp.StompSubframeAggregator;
import io.netty.handler.codec.stomp.StompSubframeDecoder;
import io.netty.handler.codec.stomp.StompSubframeEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import static com.rj.stomp.util.Constant.*;

/**
 * @desc Stomp(via Websocket) 初始化
 * @author larryjay
 * @since 2023/9/19 20:45
*/
public class StompWebSocketInitializer extends ChannelInitializer<Channel> {

    /**uri路径*/
    private String websocketPath;

    public StompWebSocketInitializer(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //设置读写超时设置
        pipeline.addLast(HandlerName.IDLE_TIMEOUT,new IdleStateHandler(WebSocketConfig.REDER_IDLE_TIMEOUT.getValue(),
                WebSocketConfig.WRITER_IDLE_TIMEOUT.getValue(),WebSocketConfig.ALL_IDLE_TIMEOUT.getValue()));
        pipeline.addAfter(HandlerName.IDLE_TIMEOUT,HandlerName.IDLE_ENVET,
                new CommonChannelIdleTimeoutHandler());
        //Http Handler配置
        pipeline.addLast(HandlerName.HTTP_CODEC,new HttpServerCodec());
        pipeline.addLast(HandlerName.HTTP_AGGREGATOR,
                new HttpObjectAggregator(Constant.WebSocketConfig.MAX_FRAME_SIZE.getValue()));
        //Websocket Handler配置
        pipeline.addLast(HandlerName.WEBSOCKET_HANDLER,new WebSocketServerProtocolHandler(websocketPath, StompVersion.SUB_PROTOCOLS));
        pipeline.addLast(HandlerName.BUFF_WEBSOCKET_Encoder,new ByteBufToWebSocketFrameEncoder());
        pipeline.addLast(HandlerName.BUFF_WEBSOCKET_Decoder,new WebSocketFrameToByteBufDecoder());
//        pipeline.addLast(new WebSocketByteTestHandler());
        //StompHandler配置
        pipeline.addLast(HandlerName.STOMP_DECODER,new StompSubframeDecoder());
        pipeline.addLast(HandlerName.STOMP_ENCODER,new StompSubframeEncoder());
        pipeline.addLast(HandlerName.STOMP_AGGREGATOR,new StompSubframeAggregator(WebSocketConfig.MAX_FRAME_SIZE.getValue()));
        pipeline.addLast(HandlerName.STOMP_HANDLER,new StompWebSocketHandler());

    }
}
