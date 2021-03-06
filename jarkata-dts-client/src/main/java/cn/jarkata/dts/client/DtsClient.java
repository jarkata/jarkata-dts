package cn.jarkata.dts.client;

import cn.jarkata.dts.client.channel.JarkataChannel;
import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.common.utils.FileUtils;
import cn.jarkata.protobuf.DataMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.jarkata.dts.common.constant.JarkataConstant.CLIENT_BASE_PATH;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_HOST;

/**
 * Hello world!
 */
public class DtsClient {
    private static final Logger logger = LoggerFactory.getLogger(DtsClient.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            transfer();
        } finally {
            long dur = System.currentTimeMillis() - start;
            logger.info("耗时:{}ms", dur);
        }
        System.exit(-1);
    }

    private static void transfer() throws Exception {
        String basePath = Env.getProperty(CLIENT_BASE_PATH);
        List<String> fileList = FileUtils.getFileList(basePath);
        logger.info("BasePath={},FileSize={}", basePath, fileList.size());
        String serverHost = Env.getProperty(SERVER_HOST);
        logger.info("服务端地址:{}", serverHost);
        String[] split = serverHost.split(":");
        assert split.length != 2;
        JarkataChannel jarkataChannel = new JarkataChannel(split[0], Integer.parseInt(split[1]));
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        int index = basePath.lastIndexOf("/");
        if (index > 0) {
            basePath = basePath.substring(0, index);
        }
        for (String path : fileList) {
            String filePath = FileUtils.getRelativePath(basePath, path);
            logger.info("FilePath={}", filePath);
            byte[] array = IOUtils.toByteArray(new FileInputStream(path));
            DataMessage dataMessage = new DataMessage(filePath, array);
            jarkataChannel.writeFile(dataMessage);
        }
        jarkataChannel.waitForFinish(TimeUnit.MINUTES.toMillis(30));
        System.exit(-1);
    }

}
