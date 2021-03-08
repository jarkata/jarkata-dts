package cn.jarkata.dts.server;

import cn.jarkata.commons.concurrent.ThreadPoolFactory;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.config.ServerConfig;
import cn.jarkata.dts.handler.DataTransferHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT_DEFAULT_VAL;

public class TransferFileServer extends AbstractServer {

    private final ServerConfig serverConfig;
    ExecutorService handlerPoolExecutor;

    public TransferFileServer() {
        super(Env.getIntProperty(SERVER_PORT, SERVER_PORT_DEFAULT_VAL));
        this.serverConfig = new ServerConfig();
        handlerPoolExecutor = ThreadPoolFactory.newThreadPool("handler",
                serverConfig.getIoThreadCount(), serverConfig.getIoThreadCount(), 10000,
                60 * 1000, 60 * 1000);
    }

    @Override
    EventLoopGroup getBossEventGroup() {
        return TransportUtils.getEventGroup(0, "boss-group");
    }

    @Override
    EventLoopGroup getWorkEventGroup() {
        return TransportUtils.getEventGroup(serverConfig.getWorkThreadCount(), "work-group");
    }

    @Override
    Function<ChannelPipeline, Void> getChannelFunction() {
        return pipeline -> {
            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline.addLast(new DataTransferHandler(handlerPoolExecutor))
                    .addLast(new ProtobufVarint32LengthFieldPrepender());
            return null;
        };
    }
}
