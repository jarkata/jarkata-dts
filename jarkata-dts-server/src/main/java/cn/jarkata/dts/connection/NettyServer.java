package cn.jarkata.dts.connection;

import cn.jarkata.dts.handler.DataTransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workLoopGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_BACKLOG, 8 * 1024);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioServerSocketChannel.class)
                .group(eventLoopGroup, workLoopGroup)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
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
