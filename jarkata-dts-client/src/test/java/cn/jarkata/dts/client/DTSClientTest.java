package cn.jarkata.dts.client;

import cn.jarkata.commons.utils.NetUtils;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.FileUtils;
import io.netty.util.NetUtil;
import org.junit.Test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

import static cn.jarkata.dts.common.constant.JarkataConstant.CLIENT_BASE_PATH;

/**
 * Unit test for simple App.
 */
public class DTSClientTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String basePath = Env.getProperty(CLIENT_BASE_PATH);
        List<String> fileList = FileUtils.getFileList(basePath);
        for (String fullPath : fileList) {
            if (NettyClient.allowTransfer(basePath, fullPath)) {
            }
        }
    }

    @Test
    public void test() throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println(localHost);
        String inet4Address = NetUtils.getInet4Address();
        Inet6Address byName = NetUtil.getByName("182.168.0.1");
        System.out.println(byName);
        System.out.println(inet4Address);
    }
}
