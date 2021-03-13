package cn.jarkata.dts.client;

import cn.jarkata.dts.common.MessageEncode;
import cn.jarkata.protobuf.ChuckDataMessage;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
public class DTSClient {

    private static final Logger logger = LoggerFactory.getLogger(DTSClient.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            File file = new File("/Users/vkata/data1/0001.mov");
            System.out.println(file.length());
            AtomicInteger count = new AtomicInteger();
            MessageEncode messageEncode = new MessageEncode();
            messageEncode.encodeChuckStream("/Users/vkata/data1", file, (message) -> {
                ByteBuf buffer = null;
                try {
                    buffer = message.encode();
                    count.getAndIncrement();
                    ChuckDataMessage messageData = ChuckDataMessage.decode(buffer);
                    System.out.println(count.get() + "," + messageData);
                    messageEncode.decode("/Users/vkata/data4", messageData);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Optional.ofNullable(buffer).ifPresent(ByteBuf::clear);
                }
            });
//            NettyClient.transfer(args);
        } finally {
            long dur = System.currentTimeMillis() - start;
            logger.info("耗时:{}ms", dur);
        }
    }
}
