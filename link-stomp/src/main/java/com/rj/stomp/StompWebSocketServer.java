package com.rj.stomp;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.rj.netty.impl.AbstractNettyServer;
import com.rj.stomp.initializer.StompWebSocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class StompWebSocketServer extends AbstractNettyServer {
    public static final Log log = LogFactory.get();

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private String path;

    public StompWebSocketServer(String path) {
        this.path = path;
    }

    private ChannelInitializer createChannelInitializer(){
        return new StompWebSocketInitializer(path);
    }

    @Override
    public void start() throws Exception {
        ServerBootstrap bootstrap =  new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(createChannelInitializer());
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        log.info("StompWebSocketServer started on port " + port);
                    }else {
                        log.error("StompWebSocketServer failed to start on port " + port +  " due to " + future.cause());
                    }
                }
            });
            future.syncUninterruptibly();
            Channel channel = future.channel();
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    try {
                        shutdown();
                    } catch (Exception e) {
                        log.error("Stomp服务销毁异常：" + e.getMessage());
                    }
                }
            });
            channel.closeFuture().syncUninterruptibly();
        }

    /**
       * @desc 关闭资源
       */
    public void shutdown() throws Exception{
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
