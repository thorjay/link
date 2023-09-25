package com.rj.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @desc 心跳机制
 * @author larryjay
 * @since 2023/9/18 15:58
*/
public class HeartBeatHandler extends ChannelDuplexHandler {

    private static final Logger loogger = LoggerFactory.getLogger(HeartBeatHandler.class);
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
//                loogger.debug("[关闭读空闲的连接]:{clientId:" + NettyAttrUtil.takeClientId(ctx.channel()) + ", channel:" + ctx.channel() + "}");
                ctx.close(); //关闭连接会触发ctx.fireChannelInactive，避免调用两次
            } else if (event.state() == IdleState.WRITER_IDLE){
//                loogger.debug("[关闭写空闲的连接]:{clientId:" + NettyAttrUtil.takeClientId(ctx.channel()) + ", channel:" + ctx.channel() + "}");
                ctx.close(); //关闭连接会触发ctx.fireChannelInactive，避免调用两次
            }else{
                //不是 IdleStateEvent事件，所以将它传递给下一个 ChannelInboundHandler
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
