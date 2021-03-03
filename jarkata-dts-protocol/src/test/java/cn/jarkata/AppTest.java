package cn.jarkata;

import cn.jarkata.common.protobuf.ObjectInput;
import cn.jarkata.common.protobuf.ObjectOutput;
import cn.jarkata.common.serializer.FileMessage;
import org.junit.Test;

import java.io.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        ObjectOutput output = new ObjectOutput(bos);
        File file = new File("/Users/vkata/code/design/README.md");
        FileMessage fileMessage = new FileMessage("README.md", "md", new FileInputStream(file));
        output.write(fileMessage);
        output.flushBuffer();
        bos.flush();
        byte[] bytes = bos.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput input = new ObjectInput(inputStream);
        FileMessage o = input.readObject();
        byte[] stream = o.getStream();
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(stream));
        FileOutputStream fos = new FileOutputStream(new File("./test"));
        byte[] dist = new byte[1024];
        int len = 0;
        while ((len = bis.read(dist)) != -1) {
            fos.write(dist, 0, len);
        }
        fos.close();

    }
}
