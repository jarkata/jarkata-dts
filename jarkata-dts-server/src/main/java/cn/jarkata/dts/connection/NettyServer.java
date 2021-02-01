package cn.jarkata.dts.connection;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.dts.handler.DataTransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(new NamedThreadFactory("boos-group"));
        EventLoopGroup workLoopGroup = new NioEventLoopGroup(1000, new NamedThreadFactory("work-group"));
        //
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(eventLoopGroup, workLoopGroup);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 8 * 1024);
//        bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(30, 30, 30));
                pipeline.addLast(new DataTransferHandler());
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
}
