package cn.jarkata.dts.server;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.handler.ShutdownHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;

import java.util.function.Function;

import static cn.jarkata.dts.common.constant.JarkataConstant.SHUTDOWN_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SHUTDOWN_PORT_DEFAULT_VAL;

public class ShutdownServer extends AbstractServer {

    public ShutdownServer() {
        super(Env.getIntProperty(SHUTDOWN_PORT, SHUTDOWN_PORT_DEFAULT_VAL));
    }

    @Override
    EventLoopGroup getBossEventGroup() {
        return TransportUtils.getEventGroup(1, "shutdown-group");
    }

    @Override
    EventLoopGroup getWorkEventGroup() {
        return TransportUtils.getEventGroup(1, "shutdown-group");
    }

    @Override
    Function<ChannelPipeline, Void> getChannelFunction() {
        return pipeline -> {
            pipeline.addLast(new ShutdownHandler());
            return null;
        };
    }

}
