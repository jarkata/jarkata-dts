package cn.jarkata.dts.client;

import cn.jarkata.dts.client.channel.JarkataChannel;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.jarkata.dts.common.constant.JarkataConstant.*;

public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    /**
     * 传输文件
     *
     * @param args 传入的控制参数
     * @throws Exception 发生异常时，抛出此异常
     */
    public static void transfer(String[] args) throws Exception {
        String basePath = Env.getProperty(CLIENT_BASE_PATH);
        List<String> fileList = FileUtils.getFileList(basePath);
        String serverHost = Env.getProperty(SERVER_HOST);
        logger.info("服务端地址:{},BasePath={},FileSize={}", serverHost, basePath, fileList.size());
        JarkataChannel jarkataChannel = new JarkataChannel(serverHost);
        for (String fullPath : fileList) {
            if (!allowTransfer(basePath, fullPath)) {
                continue;
            }
            jarkataChannel.sendFile(fullPath);
        }
        jarkataChannel.waitForFinish(TimeUnit.MINUTES.toMillis(30));
    }

    /**
     * 是否允许传输文件
     *
     * @param basePath 基础路径
     * @param fullPath 文件路径列表
     * @return 如果允许传输，则返回true,否则返回false
     */
    public static boolean allowTransfer(String basePath, String fullPath) {
        String clientSubPath = Env.getProperty(CLIENT_SUB_PATH);
        if ("*".equalsIgnoreCase(clientSubPath)) {
            return true;
        }
        String[] split = clientSubPath.split(SUB_DIR_SEP_REGEX);
        for (String subPath : split) {
            String basePathBuilder = basePath + "/" + subPath;
            String prefixBasePath = FileUtils.trimPathSep(basePathBuilder);
            if (fullPath.startsWith(prefixBasePath)) {
                return true;
            }
        }
        return false;
    }
}
