package cn.jarkata;

import static org.junit.Assert.assertTrue;

import io.netty.util.NetUtil;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class DtsClientTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        int somaxconn = NetUtil.SOMAXCONN;
        System.out.println(somaxconn);
        assertTrue(true);
    }
}
