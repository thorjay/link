package com.rj.stomp.handler;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @desc websocket测试Hanlder，测试通过TextWebsocketFrame转ByteBuf后的处理
 * @author larryjay
 * @since 2023/9/19 20:32
*/
public class WebSocketByteTestHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final Log log = LogFactory.get();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt ==  WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            log.info("channelID" + ctx.channel().id() +  "连接成功");
//            this.group.add(ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("{} recieved message:" + msg.toString(),this.getClass().getName());
        ctx.write(msg.retain());
    }
}
