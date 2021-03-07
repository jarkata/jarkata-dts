package cn.jarkata.dts.client;

import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.FileUtils;
import org.junit.Test;

import java.util.List;

import static cn.jarkata.dts.common.constant.JarkataConstant.CLIENT_BASE_PATH;

/**
 * Unit test for simple App.
 */
public class DtsClientTest {
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
}
