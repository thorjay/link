package com.rj;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.rj.netty.INettyServer;

import java.util.HashSet;
import java.util.Set;

/**
 * @des 连接服务管理器，提供注册等功能
 * @author larryjay
 * @since 2023/9/18 11:05
*/
public class ConnectServerManager {

    private static Set<INettyServer> servers = new HashSet<>();

    private static ThreadLocal<Integer> serverPortThreadLocal = new TransmittableThreadLocal<>();

    public enum ServerType{
        MQTT("mqtt.port"),WEBSOCKET("websocket.port"),STOMP("stomp.port"),HTTP("http.port"),TCP("tcp.port"),UDP("udp.port");

        private String type;

        ServerType(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public static void registerServer(String alias,INettyServer server){
        server.setAlias(alias);
        servers.add(server);
    }

    public static Set<INettyServer> getServers(){
        return servers;
    }

    public static void saveServerPort(int port){
        serverPortThreadLocal.set(port);
    }

    public static int getServerPort(){
        return serverPortThreadLocal.get();
    }
}
