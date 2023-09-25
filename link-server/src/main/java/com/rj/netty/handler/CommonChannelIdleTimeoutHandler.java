package com.rj.netty.handler;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 服务器超时处理类：可以处理读超时、写超时和读写超时。<br/>
 * 读超时：客户端在规定时间内未上发心跳或业务数据，则被触发<br/>
 * 写超时：服务端在规定时间内未下行业务数据或未下行response信息，则被触发
 *
 * @author Administrator
 */
public class CommonChannelIdleTimeoutHandler extends ChannelDuplexHandler {
    transient static final Log log = LogFactory.get();

    /**
     * 服务器超时处理：可以处理读超时、写超时和读写超时。<br/>
     * 当触发服务器超时，服务器将关闭socket通道，并回收资源
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState e = ((IdleStateEvent) evt).state();
            if (e == IdleState.WRITER_IDLE) {
                // fire a channelInactive to trigger publish of Will
//                log.debug("[关闭写空闲的连接]:{clientId:" + NettyAttrUtil.takeClientId(ctx.channel()) + ", channel:" + ctx.channel() + "}");
                ctx.close(); //关闭连接会触发ctx.fireChannelInactive，避免调用两次
            } else if (e == IdleState.READER_IDLE) {
                // fire a channelInactive to trigger publish of Will
//                log.debug("[关闭写空闲的连接]:{clientId:" + NettyAttrUtil.takeClientId(ctx.channel()) + ", channel:" + ctx.channel() + "}");
                ctx.close(); //关闭连接会触发ctx.fireChannelInactive，避免调用两次
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
