package com.rj;

import com.rj.stomp.StompWebSocketServer;
import org.slf4j.Logger;

public class StartServer {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StartServer.class);

    public static void main(String[] args) {

        try {
            ConnectServerManager.registerServer(ConnectServerManager.ServerType.STOMP.getType(),
                    new StompWebSocketServer("/stomp"));
            ConnectServerStart.start(args);
        } catch (Exception e) {
            logger.error("启动失败：" + e.getMessage());
            try {
//                ReflectUtil.invokeStaticMethod(System.class, "exit", new Object[]{0});
            } catch (Exception e1) {
            }
        }
    }
}