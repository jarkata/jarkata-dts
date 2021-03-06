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

/**
 * Hello world!
 */
public class DtsClient {
    private static final Logger logger = LoggerFactory.getLogger(DtsClient.class);

    public static void main(String[] args) throws Exception {

        String basePath = Env.getProperty("client.base.path");
        List<String> fileList = FileUtils.getFileList(basePath);
        logger.info("FileSize={}", fileList.size());
        String serverHost = Env.getProperty("server.host");
        String[] split = serverHost.split("\\:");
        assert split.length != 2;
        JarkataChannel connection = new JarkataChannel(split[0], Integer.parseInt(split[1]));
        for (String path : fileList) {
            String filePath = FileUtils.getRelativePath(basePath, path);
            byte[] array = IOUtils.toByteArray(new FileInputStream(path));
            DataMessage dataMessage = new DataMessage(filePath, array);
            connection.writeFile(dataMessage);
        }

    }

}
