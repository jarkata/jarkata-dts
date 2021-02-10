package cn.jarkata.dts.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private String message;

    private Channel channel;

    private final CountDownLatch count = new CountDownLatch(1);

    public MessageHandler() {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        logger.info("channelRegistered初始化链接：{}", this.channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        this.message = buf.toString(StandardCharsets.UTF_8);
        count.countDown();
    }

    public String getMessage() throws Exception {
        count.await();
        return this.message;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
