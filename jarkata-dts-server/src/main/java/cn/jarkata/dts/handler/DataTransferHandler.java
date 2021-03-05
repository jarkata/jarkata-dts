package cn.jarkata.dts.handler;

import cn.jarkata.protobuf.DataMessage;
import cn.jarkata.protobuf.utils.ProtobufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        logger.info("Message:{}", msg);
        ByteBuf buf = (ByteBuf) msg;
        try {
            int length = buf.readableBytes();
            logger.info("Buf-length:{}", length);
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            DataMessage dataMessage = (DataMessage) ProtobufUtils.readObject(bytes);
            byte[] stream = dataMessage.getData();
            String filePath = "/Users/vkata/data" + dataMessage.getPath();
            int index = filePath.lastIndexOf("/");
            if (index > 0) {
                String parentPath = filePath.substring(0, index);
                File dir = new File(parentPath);
                boolean mkdirs = dir.mkdirs();
                logger.info("创建结果：{}", mkdirs);
            }

            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(stream));
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] dist = new byte[1024];
            int len;
            while ((len = bis.read(dist)) != -1) {
                fos.write(dist, 0, len);
            }
            fos.close();
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
        ByteBuf byteBuf = Unpooled.copiedBuffer(buf);
        String s = Hex.encodeHexString(byteBuf.array());
        logger.info("DubboHex:{}", s);
        logger.info("{}", buf.toString(StandardCharsets.UTF_8));
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(512, 1024);
        buffer.writeCharSequence(Hex.encodeHexString("success".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        ctx.writeAndFlush(buffer);
    }
}
