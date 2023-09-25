package com.rj.stomp.auth;

import io.netty.channel.Channel;

/**
 * @desc Stomp 鉴权认证接口
 * 用户、密码、客户端ID的有效性验证
 * @author larryjay
 * @since 2023/9/20 11:26
*/
public interface IAuthenticator {


    /**
     * 用户、密码、客户端ID的有效性验证
     *
     * @param channel stomp&mqtt通道
     * @param clientId 客户端ID
     * @param username 用户名
     * @param password 用户密码
     * @param bytePassword 字节型用户密码
     * @return boolean {@code true} 有效性验证通过，{@code false} 有效性验证不通过
     */
    boolean checkValid(Channel channel, String clientId,String username, String password, byte[] bytePassword);

}
