package com.rj.netty.impl;

import com.rj.netty.INettyServer;

/**
 * @desc 实现端口存储的默认逻辑
 * @author larryjay
 * @since 2023/9/18 14:45
*/
public abstract class AbstractNettyServer implements INettyServer {

    /**服务端口
     * 如果在子类中声明，会导致port为0，服务无法启动？
     * */
    protected int port;

    /**服务别名*/
    protected String alias;

    @Override
    public void setServerPort(int port) {
        this.port = port;
    }

    @Override
    public int getServerPort() {
        return this.port;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }
}
