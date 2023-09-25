package com.rj.stomp.processer;

import com.rj.stomp.fi.StompProcessorFunction;
import com.rj.stomp.processer.impl.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;

import static  io.netty.handler.codec.stomp.StompCommand.*;


/**
 * @desc 业务处理Stomp消息的接口
 * @author larryjay
 * @since 2023/9/19 20:54
*/
public interface IStompProcessor {

    Type CONNECT = Type.CONNECT;


    /**定义泛型工厂类*/
    public enum Type{
        CONNECT(1, StompCommand.CONNECT, ConnectProcessor::new),
        SEND(2, StompCommand.SEND, SendProcessor::new),
        SUBSCRIBE(3,StompCommand.SUBSCRIBE, SubscribeProcessor::new),
        UNSUBSCRIBE(4,StompCommand.UNSUBSCRIBE, UnSubscribeProcessor::new),

        DISCONNECT(5,StompCommand.DISCONNECT, DisConnectProcessor::new);

        public final int index;

        public final StompCommand command;

        public final StompProcessorFunction factory;

        Type(int index, StompCommand command, StompProcessorFunction factory) {
            this.index = index;
            this.command = command;
            this.factory = factory;
        }

        public int getIndex() {
            return this.index;
        }

        public  StompCommand getCommand() {
            return this.command;
        }

        public static final Type[] MAPPING = new Type[]{
          CONNECT
        };

        public static Type fromCommand(StompCommand command) throws IllegalArgumentException{
            switch (command){
                case CONNECT:
                    return CONNECT;
                case SEND:
                    return SEND;
                case SUBSCRIBE:
                    return SUBSCRIBE;
                case UNSUBSCRIBE:
                    return UNSUBSCRIBE;
                case DISCONNECT:
                    return DISCONNECT;
            }
            throw new IllegalArgumentException("Unknown command: " + command);
        }

        public static Type fromIndex(int index){
            assert index >= 0 && index < MAPPING.length;
            return MAPPING[index];
        }

    }

    public void process(ChannelHandlerContext ctx, StompFrame frame) throws Exception;
}
