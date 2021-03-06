package cn.jarkata.dts.client;

import cn.jarkata.dts.client.channel.JarkataChannel;
import cn.jarkata.protobuf.DataMessage;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 */
public class DtsClient {


    public static void main(String[] args) throws Exception {
        List<String> fileList = new ArrayList<>();
        String rootPath = "D:/00-Work";
        System.setProperty("dts.file.home", rootPath);
        getFileList(fileList, "D:/00-Work/00-万事网联");
        System.out.println(fileList.size());
        JarkataChannel connection = new JarkataChannel("192.168.0.103", 20880);
        for (String path : fileList) {
            String filePath = path.replaceFirst(rootPath, "");
            System.out.println(filePath);
            byte[] array = IOUtils.toByteArray(new FileInputStream(path));
            DataMessage dataMessage = new DataMessage(filePath, array);
            connection.writeFile(dataMessage);
        }

    }

    private static void getFileList(List<String> fileList, String filePath) {
        File tmpFile = new File(filePath);
        if (!tmpFile.exists()) {
            return;
        }
        if (!tmpFile.isDirectory()) {
            fileList.add(trimPath(tmpFile.getPath()));
        }
        File[] files = tmpFile.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            return;
        }
        for (File file : files) {
            getFileList(fileList, file.getPath());
        }
    }

    private static String trimPath(String path) {
        return path.replaceAll("\\\\", "/");
    }
}
