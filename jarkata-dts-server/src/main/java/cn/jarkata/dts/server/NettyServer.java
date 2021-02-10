package cn.jarkata.dts.server;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.commons.concurrent.ThreadPoolFactory;
import cn.jarkata.dts.handler.DataTransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public int port;

    public NettyServer(int port) {
        this.port = port;
        ScheduledThreadPoolExecutor report = ThreadPoolFactory.newSchedule("report", 1);
        report.scheduleWithFixedDelay(this::report, 1, 1, TimeUnit.SECONDS);
    }

    public void start() {
        int workThread = Integer.parseInt(System.getProperty("work.threads", "100"));
        int businessThread = Integer.parseInt(System.getProperty("business.threads", "100"));

        EventLoopGroup eventLoopGroup = null;
        EventLoopGroup workLoopGroup = null;
        EventLoopGroup defaultLoopGroup = null;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup(new NamedThreadFactory("boos-group"));
            workLoopGroup = new EpollEventLoopGroup(workThread, new NamedThreadFactory("work-group"));
        } else {
            eventLoopGroup = new NioEventLoopGroup(new NamedThreadFactory("boos-group"));
            workLoopGroup = new NioEventLoopGroup(workThread, new NamedThreadFactory("work-group"));
        }
        ExecutorService handlerPoolExecutor = ThreadPoolFactory.newThreadPool("handler",
                businessThread, businessThread, 10000, 60 * 1000, 60 * 1000);
        //
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.group(eventLoopGroup, workLoopGroup);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.SO_BACKLOG, 2048);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 16 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 16 * 1024);
        bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(30, 30, 30));
                pipeline.addLast(new DataTransferHandler(handlerPoolExecutor));
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.addListener((listener) -> {
                logger.info("启动结果：" + listener.isSuccess());
            });
        } catch (InterruptedException e) {
            logger.info("启动失败：", e);
        }

    }

    public void report() {
        try {
            Class<?> platformDependent = PlatformDependent.class;
            Field directMemoryCounter = platformDependent.getDeclaredField("DIRECT_MEMORY_COUNTER");
            directMemoryCounter.setAccessible(true);
            AtomicLong directCounter = (AtomicLong) directMemoryCounter.get(null);
            long countL = directCounter.get() / 1024;
            logger.info("DIRECT_MEMORY_COUNTER={}K", countL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
