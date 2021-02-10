package cn.jarkata.dts.client.connection;

import cn.jarkata.dts.client.handler.MessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class NettyConnection {

    private static final Logger logger = LoggerFactory.getLogger(NettyConnection.class);

    private final String host;
    private final int port;

    private static final ConcurrentHashMap<String, MessageHandler> cache = new ConcurrentHashMap<>();

    public NettyConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private MessageHandler getChannel() throws InterruptedException {
        MessageHandler handler = new MessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(handler);
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        ChannelFuture future = channelFuture.sync();
        future.addListener((listener) -> {
            logger.info("初始化连接");
        });
        return handler;
    }


    public String write(String msg) throws Exception {
        MessageHandler handler = cache.getOrDefault(host + "::" + port, getChannel());
        Channel channel = handler.getChannel();
        channel.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
        return handler.getMessage();
    }

}
