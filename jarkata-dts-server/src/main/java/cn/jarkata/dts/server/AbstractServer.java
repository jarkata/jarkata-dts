package cn.jarkata.dts.server;

import cn.jarkata.commons.utils.NetUtils;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.handler.DataTransferInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
        bootstrap.group(bossLoopGroup, workLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new DataTransferInitializer(getChannelFunction()));
        try {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.addListener((listener) -> {
                String inet4Address = NetUtils.getInet4Address();
                logger.info("IP={},PORT={},启动结果：{}", inet4Address, port, listener.isSuccess());
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
