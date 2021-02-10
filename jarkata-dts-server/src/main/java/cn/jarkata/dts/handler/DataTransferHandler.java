package cn.jarkata.dts.handler;

import cn.jarkata.dts.file.MappedFile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.codec.binary.Hex;
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
//    private MappedFile mappedFile = null;


    public DataTransferHandler(ExecutorService handlerExecutorService) {
        this.handlerExecutorService = handlerExecutorService;
//        mappedFile = new MappedFile("./file/1.log", 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            async(ctx, buf);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("异常：" + ctx, cause);
    }

    private void async(ChannelHandlerContext ctx, ByteBuf buf) {
        int logPercent = Integer.parseInt(System.getProperty("log.sample", "100"));
        logger.info("{}", buf.toString(StandardCharsets.UTF_8));
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(512, 1024);
        buffer.writeCharSequence(Hex.encodeHexString("success".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        ctx.writeAndFlush(buffer);
    }
}
