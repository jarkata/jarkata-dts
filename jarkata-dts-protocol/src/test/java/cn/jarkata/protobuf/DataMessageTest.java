package cn.jarkata.protobuf;

import cn.jarkata.protobuf.utils.ProtobufUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Unit test for simple App.
 */
public class DataMessageTest {

    @Test
    public void testProtoBuf() throws Exception {

        File file = new File("/Users/vkata/logs/tmp.txt");

        DataMessage fileMessage = new DataMessage(System.currentTimeMillis(), file.getPath(), IOUtils.toByteArray(new FileInputStream(file)));
        long start = System.currentTimeMillis();
        for (int index = 0; index < 1; index++) {
            byte[] bytes2 = ProtobufUtils.toByteArray(fileMessage);
        }
        System.out.println(System.currentTimeMillis() - start);
        long start1 = System.currentTimeMillis();
        for (int index = 0; index < 1; index++) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            try (ObjectOutputStream objs = new ObjectOutputStream(bos)) {
                objs.writeObject(fileMessage);
            }
        }
        System.out.println(System.currentTimeMillis() - start1);

    }

    @Test
    public void testRead() {
        DataMessage fileMessage = new DataMessage(System.currentTimeMillis(), "/fsdf", "test".getBytes(StandardCharsets.UTF_8));
        byte[] bytes2 = ProtobufUtils.toByteArray(fileMessage);
        DataMessage message = (DataMessage) ProtobufUtils.readObject(bytes2);
        Assert.assertEquals(fileMessage.getPath(), message.getPath());
        Assert.assertEquals(fileMessage.getSize(), message.getSize());
        Assert.assertEquals(new String(fileMessage.getData()), new String(message.getData()));
    }
}
