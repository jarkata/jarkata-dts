package cn.jarkata.dts.client.connection;

import cn.jarkata.dts.client.handler.MessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class JarkataChannel {

    private static final Logger logger = LoggerFactory.getLogger(JarkataChannel.class);

    private final String host;
    private final int port;

    private static final ConcurrentHashMap<String, MessageHandler> cache = new ConcurrentHashMap<>();

    public JarkataChannel(String host, int port) {
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
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
        buf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        buf.writeBytes("#####".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buf);
        return handler.getMessage();
    }

    public String writeFile(String msg) throws Exception {
        MessageHandler handler = cache.getOrDefault(host + "::" + port, getChannel());
        Channel channel = handler.getChannel();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
        buf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        buf.writeBytes("#####".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buf);
        return handler.getMessage();
    }
}
