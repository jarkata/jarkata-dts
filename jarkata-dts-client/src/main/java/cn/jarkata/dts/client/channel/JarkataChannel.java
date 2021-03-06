package cn.jarkata.dts.client.channel;

import cn.jarkata.dts.client.handler.ClientHandlerInitializer;
import cn.jarkata.dts.client.handler.MessageHandler;
import cn.jarkata.protobuf.DataMessage;
import cn.jarkata.protobuf.utils.ProtobufUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class JarkataChannel {

    private static final Logger logger = LoggerFactory.getLogger(JarkataChannel.class);

    private final String host;
    private final int port;

    private static final ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();

    private static final AtomicLong sendFileCount = new AtomicLong(0);

    private Channel channel;
    private final String cacheKey;

    public JarkataChannel(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        this.channel = getChannel();
        cacheKey = host + "::" + port;
        cache.put(cacheKey, channel);
    }

    private Channel getChannel() throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        MessageHandler handler = new MessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_RCVBUF, 8 * 1024)
                .option(ChannelOption.SO_SNDBUF, 8 * 1024);
        bootstrap.handler(new ClientHandlerInitializer(handler));
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        ChannelFuture future = channelFuture.sync();
        future.addListener((listener) -> {
            logger.info("初始化连接");
        });
        Runtime.getRuntime().addShutdownHook(new Thread(eventLoopGroup::shutdownGracefully));
        return handler.getChannel();
    }

    private Channel getOrCreate() throws Exception {
        Channel channel = cache.get(cacheKey);
        if (Objects.isNull(channel)) {
            this.channel = getChannel();
            cache.put(cacheKey, this.channel);
            return this.channel;
        }
        if (!channel.isOpen()) {
            channel.close();
            this.channel = getChannel();
            cache.put(cacheKey, this.channel);
            return this.channel;
        }
        return channel;
    }

    public void writeFile(DataMessage msg) throws Exception {
        Channel channel = getOrCreate();
        sendFileCount.incrementAndGet();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(1024);
        try {
            byte[] bytes = ProtobufUtils.toByteArray(msg);
            buffer.writeBytes(bytes);
            channel.writeAndFlush(buffer).addListener((listener) -> {
                sendFileCount.decrementAndGet();
            });
        } catch (Exception ex) {
            logger.error("Path=" + msg.getPath(), ex);
            throw ex;
        }
    }

    public boolean isSendFinish() {
        return sendFileCount.get() <= 0;
    }

    public void waitForFinish(long timeout) {
        long start = System.currentTimeMillis();
        while (!isSendFinish() || System.currentTimeMillis() - start <= timeout) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
