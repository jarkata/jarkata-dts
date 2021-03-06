package cn.jarkata;

import cn.jarkata.dts.common.Env;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class DtsClientTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        long millis = TimeUnit.MINUTES.toMillis(30);
        System.out.println(millis);
        String property = Env.getProperty("base.path", "/home");
        System.out.println(property);

    }
}
