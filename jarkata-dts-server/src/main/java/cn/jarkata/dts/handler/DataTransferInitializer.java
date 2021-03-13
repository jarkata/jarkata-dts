package cn.jarkata.dts.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class DataTransferInitializer extends ChannelInitializer<SocketChannel> {

    private final Logger logger = LoggerFactory.getLogger(DataTransferInitializer.class);

    Function<ChannelPipeline, Void> function;

    public DataTransferInitializer(Function<ChannelPipeline, Void> voidFunction) {
        this.function = voidFunction;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        this.function.apply(pipeline);
        pipeline.addLast(new IdleStateHandler(0, 0, 30));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("Init Failed：", cause);
    }
}
