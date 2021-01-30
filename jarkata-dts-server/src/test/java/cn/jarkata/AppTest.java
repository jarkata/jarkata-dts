package cn.jarkata;

import static org.junit.Assert.assertTrue;

import io.netty.util.NettyRuntime;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String hexstring = Hex.encodeHexString("你好，中国".getBytes());
        System.out.println(hexstring);
        assertTrue(true);
        int processors = NettyRuntime.availableProcessors();
        System.out.println(processors);
    }
}
