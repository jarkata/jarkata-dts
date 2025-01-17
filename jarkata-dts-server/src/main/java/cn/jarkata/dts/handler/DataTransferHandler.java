package cn.jarkata.dts.handler;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.FileUtils;
import cn.jarkata.protobuf.DataMessage;
import cn.jarkata.protobuf.utils.ProtobufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_BASE_PATH;

public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(DataTransferHandler.class);

    private final ExecutorService handlerExecutor;

    public DataTransferHandler(ExecutorService handlerExecutor) {
        this.handlerExecutor = handlerExecutor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.debug("Message:{}", msg);
        ByteBuf buffer = (ByteBuf) msg;
        async(ctx, buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("异常：" + ctx, cause);
    }

    /**
     * 异步执行IO
     *
     * @param buffer
     */
    private void async(ChannelHandlerContext ctx, ByteBuf buffer) {
//        handlerExecutor.execute(() -> {
        long start = System.currentTimeMillis();
        try {
            int length = buffer.readableBytes();
            logger.debug("Buf-length:{}", length);
            byte[] bytes = new byte[length];
            buffer.readBytes(bytes);
            DataMessage dataMessage = (DataMessage) ProtobufUtils.readObject(bytes);
            byte[] stream = dataMessage.getData();
            String basePath = Env.getProperty(SERVER_BASE_PATH);
            String fullPath = FileUtils.getFullPath(basePath, dataMessage.getPath());
            logger.info("fullPath={}", fullPath);
            FileUtils.ensureParentPath(fullPath);
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                IOUtils.write(stream, fos);
            } catch (IOException e) {
                logger.error("fullPath=" + fullPath, e);
            }
//            ByteBuf responseBuf = PooledByteBufAllocator.DEFAULT.buffer();
//            responseBuf.writeBytes("success".getBytes(StandardCharsets.UTF_8));
//            ctx.writeAndFlush(responseBuf).addListener((listener) -> {
//                logger.info("SendResult={}", listener.isSuccess());
//            });
        } finally {
            long dur = System.currentTimeMillis() - start;
            logger.info("文件传输耗时:{}ms", dur);
            ReferenceCountUtil.release(buffer);
        }
//        });
    }
}
