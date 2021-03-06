package cn.jarkata.dts.client.channel;

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

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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

    public String writeFile(DataMessage msg) throws Exception {
        Channel channel = getOrCreate();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(1024);
        try {
            byte[] bytes = ProtobufUtils.toByteArray(msg);
            buffer.writeBytes(bytes);
            channel.writeAndFlush(buffer);
        } catch (Exception ex) {
            logger.error("Path=" + msg.getPath(), ex);
            throw ex;
        }
        return null;
    }


    private byte[] toByteArray(File file) throws IOException {
        ByteArrayOutputStream bos = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ) {
            bos = new ByteArrayOutputStream();
            byte[] dist = new byte[1024];
            int len;
            while ((len = bis.read(dist)) != -1) {
                bos.write(dist, 0, len);
            }
            return bos.toByteArray();
        } finally {
            Optional.ofNullable(bos).ifPresent(stream -> {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.err.println("file=" + file.getPath());
                    e.printStackTrace();
                }
            });
        }
    }
}
