package cn.jarkata;

import cn.jarkata.dts.common.Env;
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

        String property = Env.getProperty("base.path", "/home");
        System.out.println(property);

    }
}
