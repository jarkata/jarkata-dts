package cn.jarkata.dts.server;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.handler.DataTransferInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

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

        EventLoopGroup eventLoopGroup;
        EventLoopGroup workLoopGroup;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup(new NamedThreadFactory("boos-group"));
            workLoopGroup = new EpollEventLoopGroup(workThread, new NamedThreadFactory("work-group"));
        } else {
            eventLoopGroup = new NioEventLoopGroup(new NamedThreadFactory("boos-group"));
            workLoopGroup = new NioEventLoopGroup(workThread, new NamedThreadFactory("work-group"));
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.group(eventLoopGroup, workLoopGroup);
        bootstrap.handler(new LoggingHandler(LogLevel.WARN));
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 16 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 16 * 1024);
//        bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
        bootstrap.childHandler(new DataTransferInitializer());
        try {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.addListener((listener) -> {
                logger.info("启动结果：" + listener.isSuccess());
            });
        } catch (InterruptedException e) {
            logger.info("启动失败：", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            eventLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }));

    }

    public void report() {
        try {
            Class<?> platformDependent = PlatformDependent.class;
            Field directMemoryCounter = platformDependent.getDeclaredField("DIRECT_MEMORY_COUNTER");
            directMemoryCounter.setAccessible(true);
            AtomicLong directCounter = (AtomicLong) directMemoryCounter.get(null);
            long countL = directCounter.get() / 1024;
            if (countL > 0) {
                logger.info("DIRECT_MEMORY_COUNTER={}K", countL);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
