package cn.jarkata.dts.server;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.commons.concurrent.ThreadPoolFactory;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.config.ServerConfig;
import cn.jarkata.dts.handler.FileDataTransferHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT_DEFAULT_VAL;

public class TransferFileServer extends AbstractServer {

    private final Logger logger = LoggerFactory.getLogger(TransferFileServer.class);

    private final ServerConfig serverConfig;
    private final ExecutorService handlerPoolExecutor;

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
        int workThreadCount = serverConfig.getWorkThreadCount();
        logger.info("workThreadCount={}", workThreadCount);
//        return TransportUtils.getEventGroup(serverConfig.getWorkThreadCount(), "work-group");
        return new NioEventLoopGroup(100, new NamedThreadFactory("work"));
    }

    @Override
    Function<ChannelPipeline, Void> getChannelFunction() {
        return pipeline -> {
            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 16, 20, 4));
            pipeline.addLast(new FileDataTransferHandler());
            return null;
        };
    }
}
