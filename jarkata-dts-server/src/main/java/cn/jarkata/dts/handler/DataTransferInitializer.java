package cn.jarkata.dts.handler;

import cn.jarkata.commons.concurrent.ThreadPoolFactory;
import cn.jarkata.dts.common.Env;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static cn.jarkata.dts.constant.JarkataConstant.SERVER_IO_THREADS;
import static cn.jarkata.dts.constant.JarkataConstant.SERVER_IO_THREADS_DEFUALT_VAL;

public class DataTransferInitializer extends ChannelInitializer<SocketChannel> {

    private final Logger logger = LoggerFactory.getLogger(DataTransferInitializer.class);

    private final ExecutorService handlerPoolExecutor;

    public DataTransferInitializer() {
        String ioThreads = Env.getProperty(SERVER_IO_THREADS, SERVER_IO_THREADS_DEFUALT_VAL);
        int businessThread = Integer.parseInt(ioThreads);
        handlerPoolExecutor = ThreadPoolFactory.newThreadPool("handler",
                businessThread, businessThread, 10000, 60 * 1000, 60 * 1000);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(30, 30, 30))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new DataTransferHandler(handlerPoolExecutor))
                .addLast(new ProtobufVarint32LengthFieldPrepender());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("初始化失败：", cause);
    }
}
