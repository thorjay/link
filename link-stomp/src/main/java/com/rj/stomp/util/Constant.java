package com.rj.stomp.util;

import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @desc 常量类
 * @author larryjay
 * @since 2023/9/19 15:29
*/
public interface Constant {
    static class HandlerName{
        /**超时设置:多少秒无数据则触发READER_IDLE事件*/
        public static final String IDLE_TIMEOUT = "idleTimeout";
        public static final String IDLE_ENVET = "idleEvent";

        /**http解码器:将请求和应答消息解码为HTTP消息*/
        public static final String HTTP_CODEC = "httpCodec";

        /**http聚合器：将HTTP消息的多个部分合成一条完整的HTTP消息*/
        public static final String HTTP_AGGREGATOR = "httpAggregator";

        /**websoket协议处理器:处理websoket协议*/
        public static final String WEBSOCKET_HANDLER = "websocketHandler";

        /**文本消息处理器:处理文本消息*/
        public static final String TEXT_WEBSOCKET_HANDLER = "textWebSocketHandler";

        /**将ByteBuf 编码为 WebsocketFrame*/
        public static final String BUFF_WEBSOCKET_Encoder= "bytebufWebsocketEncoder";

        /**将WebSocketFrame 解码为 ByteBuf*/
        public static final String BUFF_WEBSOCKET_Decoder= "WebsocketFrameDecoder";

        /**stomp编码器*/
        public static final String STOMP_ENCODER = "stompEncoder";

        /**stomp解码器*/
        public static final String STOMP_DECODER = "stompDecoder";

        /**stomp聚合器*/
        public static final String STOMP_AGGREGATOR = "stompAggregator";

        /**stomp统计器*/
        public static final String STOMP_MERICS = "stompMetrics";

        /**stomp消息业务处理器*/
        public static final String STOMP_HANDLER = "stompHandler";

    }

    static enum WebSocketConfig{
        /**最大帧大小 65536*/
        MAX_FRAME_SIZE(1024 * 64),
        /**读超时*/
        REDER_IDLE_TIMEOUT(0),
        /**写超时*/
        WRITER_IDLE_TIMEOUT(0),
        /**读写超时*/
        ALL_IDLE_TIMEOUT(0);

        private int value;

        WebSocketConfig(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    static enum StompHeader{

    }

    enum StompVersion {

        STOMP_V11("1.1", "v11.stomp"),

        STOMP_V12("1.2", "v12.stomp");

        public static final AttributeKey<StompVersion> CHANNEL_ATTRIBUTE_KEY = AttributeKey.valueOf("stomp_version");
        public static final String SUB_PROTOCOLS;

        static {
            List<String> subProtocols = new ArrayList<String>(values().length);
            for (StompVersion stompVersion : values()) {
                subProtocols.add(stompVersion.subProtocol);
            }

            SUB_PROTOCOLS = StringUtil.join(",", subProtocols).toString();
        }

        private final String version;
        private final String subProtocol;

        StompVersion(String version, String subProtocol) {
            this.version = version;
            this.subProtocol = subProtocol;
        }

        public String version() {
            return version;
        }

        public String subProtocol() {
            return subProtocol;
        }

        public static StompVersion findBySubProtocol(String subProtocol) {
            if (subProtocol != null) {
                for (StompVersion stompVersion : values()) {
                    if (stompVersion.subProtocol().equals(subProtocol)) {
                        return stompVersion;
                    }
                }
            }

            throw new IllegalArgumentException("Not found StompVersion for '" + subProtocol + "'");
        }
    }

}
