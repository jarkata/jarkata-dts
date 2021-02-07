package cn.jarkata.dts.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private AtomicLong count = new AtomicLong(0);
    private final Logger logger = LoggerFactory.getLogger(DataTransferHandler.class);

    private static final Random random = new Random();

    private ExecutorService handlerExecutorService;


    public DataTransferHandler(ExecutorService handlerExecutorService) {
        this.handlerExecutorService = handlerExecutorService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        handlerExecutorService.submit(() -> {
            int logPercent = Integer.parseInt(System.getProperty("log.sample", "100"));
            String json = buf.toString(StandardCharsets.UTF_8);
            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException ignored) {
            }
            ctx.writeAndFlush(buf);
            //输出日志-不影响响应
            if (count.getAndIncrement() % logPercent == 0) {
                logger.info("message:{}", json);
            }
        });
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("异常：" + ctx, cause);
    }
}
