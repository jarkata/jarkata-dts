package cn.jarkata;

import cn.jarkata.protobuf.DataMessage;
import cn.jarkata.protobuf.utils.ProtobufUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testProtoBuf() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        File file = new File("/Users/vkata/logs/tmp.txt");
        DataMessage fileMessage = new DataMessage(file.getPath(), IOUtils.toByteArray(new FileInputStream(file)));
        long start = System.currentTimeMillis();
        byte[] bytes2 = ProtobufUtils.toByteArray(fileMessage);
        System.out.println(System.currentTimeMillis() - start);

        System.out.println("Proto:" + bytes2.length);
        long start1 = System.currentTimeMillis();

        try (ObjectOutputStream objs = new ObjectOutputStream(bos)) {
            objs.writeObject(fileMessage);
        }
        byte[] bytes = bos.toByteArray();
        System.out.println(System.currentTimeMillis() - start1);
        System.out.println("Java:" + bytes.length);
        DataMessage message = (DataMessage) ProtobufUtils.readObject(bytes2);
        System.out.println(message);
    }
}
