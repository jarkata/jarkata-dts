package cn.jarkata.dts.handler;

import cn.jarkata.commons.concurrent.ThreadPoolFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class DataTransferInitializer extends ChannelInitializer<SocketChannel> {

    private Logger logger = LoggerFactory.getLogger(DataTransferInitializer.class);

    private final ExecutorService handlerPoolExecutor;

    public DataTransferInitializer() {
        int businessThread = Integer.parseInt(System.getProperty("business.threads", "100"));
        handlerPoolExecutor = ThreadPoolFactory.newThreadPool("handler",
                businessThread, businessThread, 10000, 60 * 1000, 60 * 1000);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(30, 30, 30));
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024 * 1024, Unpooled.wrappedBuffer("#####".getBytes(StandardCharsets.UTF_8))));
        pipeline.addLast(new DataTransferHandler(handlerPoolExecutor));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("初始化失败：", cause);
    }
}
