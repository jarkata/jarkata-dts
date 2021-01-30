package cn.jarkata.dts.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(DataTransferHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String json = buf.toString(StandardCharsets.UTF_8);
        logger.info("message:{}", json);
        ByteBuf buffer = Unpooled.buffer(8);
        buffer.writeBytes("00000000".getBytes());
        ctx.writeAndFlush(buffer);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("异常：" + ctx, cause);
        ctx.close();
    }
}
