package com.rj.netty;

/**
 * @des 抽象出Netty服务连接的公共功能
 * @author larryjay
 * @since 2023/9/18 10:38
*/
public interface INettyServer {

    /**启动服务*/
    void start() throws Exception;

    /**关闭服务*/
    void shutdown() throws Exception;

    /**设置服务端口*/
    void setServerPort(int port);

    /**获取服务端口*/
    int getServerPort();

    /**连接服务端口 别名*/
    void setAlias(String alias);

    String getAlias();

}
