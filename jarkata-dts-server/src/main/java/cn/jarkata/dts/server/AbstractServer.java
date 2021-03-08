package cn.jarkata.dts.server;

import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.handler.DataTransferInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.Function;

public abstract class AbstractServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);
    private final int port;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossLoopGroup = getBossEventGroup();
        EventLoopGroup workLoopGroup = getWorkEventGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(TransportUtils.getServerSocketChannel());
        bootstrap.group(bossLoopGroup, workLoopGroup);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 8 * 1024);
        bootstrap.childHandler(new DataTransferInitializer(getChannelFunction()));
        try {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.addListener((listener) -> {
                logger.info("启动结果：" + listener.isSuccess());
            });
        } catch (Exception e) {
            logger.info("启动失败：", e);
        } finally {
            TransportUtils.closeEventLoopGroup(bossLoopGroup, workLoopGroup);
        }
    }

    abstract EventLoopGroup getBossEventGroup();

    abstract EventLoopGroup getWorkEventGroup();

    abstract Function<ChannelPipeline, Void> getChannelFunction();

}
