package cn.jarkata.dts.common.stream;

import cn.jarkata.dts.common.MessageEncode;
import cn.jarkata.protobuf.ChuckDataMessage;
import io.netty.buffer.ByteBuf;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class FileChuckStreamTest {

    @Test
    public void test() throws IOException {
        File file = new File("/Users/vkata/data1/0001.mov");
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
    }
}