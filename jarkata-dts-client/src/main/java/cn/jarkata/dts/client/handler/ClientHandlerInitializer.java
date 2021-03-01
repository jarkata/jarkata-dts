package cn.jarkata.dts.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientHandlerInitializer extends ChannelInitializer<NioSocketChannel> {

    private final ChannelHandler channelHandler;

    public ClientHandlerInitializer(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        nioSocketChannel.pipeline().addLast(channelHandler)
                .addLast(new IdleStateHandler(30, 30, 30));

    }
}
