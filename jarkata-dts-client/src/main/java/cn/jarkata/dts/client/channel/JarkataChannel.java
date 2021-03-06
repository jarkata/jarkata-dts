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

import static cn.jarkata.dts.common.constant.JarkataConstant.HOST_SEPARATOR;

public class JarkataChannel {

    private static final Logger logger = LoggerFactory.getLogger(JarkataChannel.class);

    private final String host;
    private final int port;

    private static final ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();

    private static final AtomicLong sendFileCount = new AtomicLong(0);

    private Channel channel;
    private final String cacheKey;

    public JarkataChannel(String serverHost) throws Exception {
        String[] split = serverHost.split(HOST_SEPARATOR);
        assert split.length != 2;
        this.host = split[0];
        this.port = Integer.parseInt(split[1]);
        this.channel = getChannel();
        this.cacheKey = host + "::" + port;
        cache.put(cacheKey, channel);
    }

    /**
     * 创建连接
     *
     * @return 连接信息
     * @throws Exception 获取连接时发生的异常
     */
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
        future.addListener((listener) -> logger.info("初始化连接"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            eventLoopGroup.shutdownGracefully();
            logger.info("客户端关闭成功");
        }));
        return handler.getChannel();
    }

    /**
     * 创建渠道信息
     *
     * @return 渠道对象
     * @throws Exception 发生异常时抛出此异常
     */
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

    /**
     * 写文件
     *
     * @param msg 文件消息
     * @throws Exception 写文件异常
     */
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

    /**
     * 是否完成
     *
     * @return true-表示完成,false-表示未完成
     */
    public boolean isSendFinish() {
        return sendFileCount.get() <= 0;
    }

    /**
     * 等待完成
     *
     * @param timeout
     */
    public void waitForFinish(long timeout) {
        long start = System.currentTimeMillis();
        while (!isSendFinish()) {
            if (System.currentTimeMillis() - start > timeout) {
                break;
            }
            try {
                Thread.sleep(100L);
            } catch (Exception ignored) {
            }
        }
        logger.info("发送完成结果:{}", isSendFinish());
    }
}
