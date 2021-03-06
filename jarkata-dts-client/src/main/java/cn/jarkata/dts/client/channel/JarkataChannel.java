package cn.jarkata.dts.client.channel;

import cn.jarkata.commons.idcreator.IdFactory;
import cn.jarkata.dts.client.handler.ClientHandlerInitializer;
import cn.jarkata.dts.client.handler.MessageHandler;
import cn.jarkata.protobuf.DataMessage;
import cn.jarkata.protobuf.utils.ProtobufUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JarkataChannel {

    private static final Logger logger = LoggerFactory.getLogger(JarkataChannel.class);

    private final String host;
    private final int port;

    private static final ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();

    public JarkataChannel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Channel getChannel() throws InterruptedException {
        MessageHandler handler = new MessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientHandlerInitializer(handler));
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        ChannelFuture future = channelFuture.sync();
        future.addListener((listener) -> {
            logger.info("初始化连接");
        });
        return handler.getChannel();
    }

    private Channel getOrCreate() throws Exception {
        String cacheKey = host + "::" + port;
        Channel channel = cache.get(cacheKey);
        if (Objects.isNull(channel)) {
            channel = getChannel();
            cache.put(cacheKey, channel);
        }
        if (!channel.isOpen()) {
            channel.close();
            channel = getChannel();
            cache.put(cacheKey, channel);
        }
        return channel;
    }

    public String writeFile(File msg) throws Exception {
        Channel channel = getOrCreate();
        long tid = IdFactory.createId(1);
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
        byte[] byteArray = IOUtils.toByteArray(new FileInputStream(msg));
        byte[] bytes = ProtobufUtils.toByteArray(new DataMessage(tid, msg.getPath(), byteArray));
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);
        return null;
    }
}
