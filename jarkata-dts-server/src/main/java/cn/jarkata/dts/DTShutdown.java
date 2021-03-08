package cn.jarkata.dts;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.TransportUtils;
import cn.jarkata.dts.handler.DataTransferInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static cn.jarkata.dts.common.constant.JarkataConstant.SHUTDOWN_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SHUTDOWN_PORT_DEFAULT_VAL;

public class DTShutdown {

    private static final Logger logger = LoggerFactory.getLogger(DTShutdown.class);
    public static final String SHUTDOWN_HOST_LOCALHOST = "localhost";


    public static void main(String[] args) throws InterruptedException {
        logger.info("准备关闭服务");
        EventLoopGroup clientEventLoopGroup = TransportUtils.getEventGroup(1, "shutdown-group");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientEventLoopGroup);
        bootstrap.channel(TransportUtils.getClientChannel());
        bootstrap.handler(new DataTransferInitializer(pipeline -> null));
        int shutdownPort = Env.getIntProperty(SHUTDOWN_PORT, SHUTDOWN_PORT_DEFAULT_VAL);
        ChannelFuture channelFuture = bootstrap.connect(SHUTDOWN_HOST_LOCALHOST, shutdownPort);
        ChannelFuture future = channelFuture.sync();
        future.addListener((listener) -> logger.info("初始化连接"));
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("shutdown".getBytes(StandardCharsets.UTF_8));
        future.channel().writeAndFlush(buffer).addListener((listener) -> System.exit(0));

    }
}
