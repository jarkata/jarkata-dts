package cn.jarkata.dts.common.utils;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportUtils {

    private static final Logger logger = LoggerFactory.getLogger(TransportUtils.class);

    public static EventLoopGroup getEventGroup(int threadCount, String groupName) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(threadCount, new NamedThreadFactory(groupName));
        }
        return new NioEventLoopGroup(threadCount, new NamedThreadFactory(groupName));
    }

    public static void closeEventLoopGroup(EventLoopGroup... loopGroups) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (EventLoopGroup loopGroup : loopGroups) {
                loopGroup.shutdownGracefully();
            }
            logger.info("客户端关闭成功");
        }));
    }

    public static Class<? extends ServerChannel> getServerSocketChannel() {
        return Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getClientChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }


}
