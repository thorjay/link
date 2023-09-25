package com.rj.stomp.auth;

public class AuthEntity {

    private String tokenId;

    private String clientId;

    public AuthEntity(String tokenId, String clientId) {
        this.tokenId = tokenId;
        this.clientId = clientId;
    }
}
