package cn.jarkata.dts.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientHandlerInitializer extends ChannelInitializer<NioSocketChannel> {

    private final ChannelHandler channelHandler;

    public ClientHandlerInitializer(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(30, 30, 30))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(channelHandler)
                .addLast(new ProtobufVarint32LengthFieldPrepender());
    }
}
