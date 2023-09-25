package com.rj.stomp.auth;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class AuthManager {

    public static final Log log = LogFactory.get();

    private static volatile AuthManager INSTANCE;

    private IAuthenticator authenticator;

    public static AuthManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AuthManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthManager();
                }
            }
        }
        return INSTANCE;
    }

    public void register(IAuthenticator authenticator){
        this.authenticator = authenticator;
    }

    public IAuthenticator getAuthenticator(){
        return this.authenticator;
    }

}
