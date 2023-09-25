package com.rj.stomp.auth.impl;

import com.rj.stomp.auth.IAuthenticator;
import io.netty.channel.Channel;

public class DefaultAuthenticator implements IAuthenticator {

    @Override
    public boolean checkValid(Channel channel, String clientId, String username, String password, byte[] bytePassword) {
        return true;
    }

}
