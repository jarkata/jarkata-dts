package cn.jarkata.dts.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private AtomicLong count = new AtomicLong(0);
    private final Logger logger = LoggerFactory.getLogger(DataTransferHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String json = buf.toString(StandardCharsets.UTF_8);
        ctx.writeAndFlush(buf);
        if (count.getAndIncrement() % 100 == 0) {
            logger.info("message:{}", json);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("异常：" + ctx, cause);
    }
}
