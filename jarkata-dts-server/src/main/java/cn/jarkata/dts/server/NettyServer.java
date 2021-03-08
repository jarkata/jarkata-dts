package cn.jarkata.dts.server;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.handler.DataTransferInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_WORK_THREADS;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_WORK_THREADS_DEFAULT_VAL;

public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        int workThread = Integer.parseInt(Env.getProperty(SERVER_WORK_THREADS, SERVER_WORK_THREADS_DEFAULT_VAL));

        EventLoopGroup eventLoopGroup = TransportUtils.getEventGroup(0, "boss-group");
        EventLoopGroup workLoopGroup = TransportUtils.getEventGroup(workThread, "work-group");

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(TransportUtils.getServerSocketChannel());
        bootstrap.group(eventLoopGroup, workLoopGroup);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 8 * 1024);
//        bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
        bootstrap.childHandler(new DataTransferInitializer());
        try {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.addListener((listener) -> {
                logger.info("启动结果：" + listener.isSuccess());
            });
        } catch (Exception e) {
            logger.info("启动失败：", e);
        } finally {
            TransportUtils.closeEventLoopGroup(eventLoopGroup, workLoopGroup);
        }

    }
}
