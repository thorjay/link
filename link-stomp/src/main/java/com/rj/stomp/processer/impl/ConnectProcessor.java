package com.rj.stomp.processer.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.stomp.auth.AuthManager;
import com.rj.stomp.auth.IAuthenticator;
import com.rj.stomp.auth.impl.DefaultAuthenticator;
import com.rj.stomp.processer.IStompProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

/**
 * @desc Stomp Connect连接命令处理
 * Stomp协议的3个部分，Command，Headers，Body，其中Command是必须的，Headers和Body是可选的。
 * 其中除了Send，Message,Error，其他都没有Body
 * @author larryjay
 * @since 2023/9/20 10:35
*/
public class ConnectProcessor implements IStompProcessor {

    public static final Log log = LogFactory.get();

    @Override
    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception {
        /**鉴权login和pass*/
        String login = frame.headers().getAsString(StompHeaders.LOGIN);
        String passcode = frame.headers().getAsString(StompHeaders.PASSCODE);
        /**服务器*/
        String host = frame.headers().getAsString(StompHeaders.HOST);
        /**版本号*/
        String acceptVersion = frame.headers().getAsString(StompHeaders.ACCEPT_VERSION);
        /**注册一个鉴权工具*/
        AuthManager.getInstance().register(new DefaultAuthenticator());

        String clientId = login;
        log.info("客户端请求连接的信息:{login:{},pass:{},host:{},acceptVersion:{}}",clientId,passcode,host,acceptVersion);
        /**缓存处理*/
        Channel channel = ctx.channel();

        /**鉴权*/
        IAuthenticator authenticator = AuthManager.getInstance().getAuthenticator();
        boolean isValid = authenticator.checkValid(channel,clientId,clientId,passcode,null);
        if(!isValid){
            Exception e = new IllegalArgumentException("鉴权失败");
            log.error(e,"{} 鉴权失败！",ConnectProcessor.class.getName());
            throw e;
        }

        /**构建Connected*/
        StompFrame connectedFrame = new DefaultStompFrame(StompCommand.CONNECTED);
        connectedFrame.headers().add(StompHeaders.ACCEPT_VERSION, acceptVersion);
        ctx.channel().writeAndFlush(connectedFrame);
    }
}
