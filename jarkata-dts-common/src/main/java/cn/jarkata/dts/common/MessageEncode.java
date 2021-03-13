package cn.jarkata.dts.common;

import cn.jarkata.dts.common.utils.FileUtils;
import cn.jarkata.protobuf.ChuckDataMessage;

import java.io.*;
import java.util.LinkedList;

public class MessageEncode {

    private static final int total = ChuckDataMessage.CHUCK_SIZE;

    public static LinkedList<ChuckDataMessage> encode(String basePath, File file) throws IOException {
        LinkedList<ChuckDataMessage> dataMessageList = new LinkedList<>();
        String relativePath = FileUtils.getRelativePath(basePath, file.getPath());
        long tid = System.currentTimeMillis();

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fileInputStream);) {
            int available = fileInputStream.available();
            int len;
            int preLen = 0;
            while (true) {
                int leaveLen = available - preLen;
                if (leaveLen <= 0) {
                    break;
                }
                int chuckSize = Math.min(leaveLen, total);
                byte[] data = new byte[chuckSize];
                if ((len = bis.read(data)) == -1) {
                    break;
                }
                ChuckDataMessage dataMessage = new ChuckDataMessage(tid, relativePath)
                        .setStartPostion(preLen)
                        .setTotalSize(available)
                        .setData(data);
                dataMessageList.add(dataMessage);
                preLen = preLen + len;
            }
        }
        return dataMessageList;
    }

    public static void decode(String basePath, ChuckDataMessage dataMessage) throws IOException {
        String fullPath = FileUtils.getFullPath(basePath, dataMessage.getPath());
        FileUtils.ensureParentPath(fullPath);
        try (RandomAccessFile accessFile = new RandomAccessFile(new File(fullPath), "rw")) {
            int startPostion = dataMessage.getStartPostion();
            accessFile.seek(startPostion);
            accessFile.write(dataMessage.getData());
        }
    }
}
