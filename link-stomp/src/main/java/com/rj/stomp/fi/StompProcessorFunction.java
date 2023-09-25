package com.rj.stomp.fi;

import com.rj.stomp.processer.IStompProcessor;
import io.netty.handler.codec.stomp.StompCommand;


/**
 * @desc 处理器工厂类
 * @author larryjay
 * @since 2023/9/19 21:04
*/
@FunctionalInterface
public interface StompProcessorFunction {

    IStompProcessor create();

}
