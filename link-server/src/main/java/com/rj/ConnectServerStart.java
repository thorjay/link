package com.rj;

import com.rj.netty.INettyServer;
import org.slf4j.Logger;

import java.util.Set;

/**
 * @des 连接服务启动类
 * @author larryjay
 * @since 2023/9/18 10:34
*/
public class ConnectServerStart {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConnectServerStart.class);

    public static void start(String[] args,boolean firstIsMain) throws Exception {
        init();

        Set<INettyServer> servers = ConnectServerManager.getServers();
        for(INettyServer server : servers){
            String alias = server.getAlias();
            int port = Integer.parseInt(System.getProperty(alias,"0"));
            if(port <= 0){
                logger.error("启动失败:" + server.getAlias() + "服务指定端口");
            }
            server.setServerPort(port);
            logger.info("准备启动服务:" + alias + "端口:" + port);
        }
        try {
            start(servers.toArray(new INettyServer[0]),firstIsMain);
        } catch (Exception e) {
            logger.error("启动异常:" + e.getMessage());
            throw e;
        }

    }

    public static void start(String[] args) throws Exception {
        start(args,true);
    }

    private static void start(INettyServer[] servers,boolean firstIsMain) throws Exception {
        for(int i = 1;i < servers.length; i++){
            startServer(servers[i]);
        }
        //首服务是否主线程启动
        if(firstIsMain){
            ConnectServerManager.saveServerPort(servers[0].getServerPort());
            servers[0].start();
        }{
            startServer(servers[0]);
        }
    }


    private static void startServer(final INettyServer server){
        //新启线程启动Netty服务
        Thread serverThread = new Thread(){
            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception e) {
                    logger.error("启动异常:" + e.getMessage() +  "服务:" + server.getAlias() + "端口:" + server.getServerPort());
                    throw new RuntimeException(e);
                }
            }
        };
        //设置为守护线程
        serverThread.setDaemon(true);
        serverThread.start();
    }


     /**
       * @des 服务启动前的操作
       */
    private static void init(){
    }
}
