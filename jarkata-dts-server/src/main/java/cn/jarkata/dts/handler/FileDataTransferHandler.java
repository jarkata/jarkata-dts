package cn.jarkata.dts.handler;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.MessageEncode;
import cn.jarkata.protobuf.ChuckDataMessage;
import cn.jarkata.protobuf.DataMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_BASE_PATH;

public class FileDataTransferHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(FileDataTransferHandler.class);

    private static final AtomicReference<DataMessage> cache = new AtomicReference<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            logger.error("Not Support Msg:{}", msg);
            return;
        }
        ByteBuf buf = (ByteBuf) msg;
        try {
            ChuckDataMessage message = ChuckDataMessage.decode(buf);
            String basePath = Env.getProperty(SERVER_BASE_PATH);
            MessageEncode.decode(basePath, message);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Case=" + ctx, cause);
    }
}
