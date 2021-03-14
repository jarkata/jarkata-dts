package cn.jarkata.dts.common;

import cn.jarkata.dts.common.stream.FileChuckStream;
import cn.jarkata.dts.common.stream.PreFunction;
import cn.jarkata.dts.common.utils.FileUtils;
import cn.jarkata.protobuf.ChuckDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MessageEncode {

    private final Logger logger = LoggerFactory.getLogger(MessageEncode.class);

    private static final int CHUCK_SIZE = ChuckDataMessage.CHUCK_SIZE;


    /**
     * 编码大文件，将大文件拆分
     *
     * @param basePath
     * @param file
     * @param function
     * @throws IOException
     */
    public void encodeChuckStream(String basePath, File file, PreFunction<ChuckDataMessage> function) throws IOException {
        if (file.isHidden()) {
            logger.warn("忽略隐藏文件:{}", file);
            return;
        }
        String relativePath = FileUtils.getRelativePath(basePath, file.getPath());
        try (FileChuckStream stream = new FileChuckStream(file, CHUCK_SIZE)) {
            process(relativePath, stream, function);
        }
    }

    /**
     * 处理流文件
     *
     * @param relativePath 相对路径
     * @param stream       流数据
     * @param function     回调方法
     * @throws IOException
     */
    private void process(String relativePath, FileChuckStream stream, PreFunction<ChuckDataMessage> function) throws IOException {
        byte[] data;
        long tid = System.currentTimeMillis();
        long preLen = 0;
        while ((data = stream.read()) != null) {
            long length = stream.getLength();
            makeMessage(tid, relativePath, preLen, length, data, function);
            preLen = preLen + data.length;
        }
    }

    /**
     * 组织数据报文
     *
     * @param tid
     * @param relativePath
     * @param preLen
     * @param length
     * @param data
     * @param function
     * @throws IOException
     */
    private void makeMessage(long tid, String relativePath, long preLen, long length,
                             byte[] data, PreFunction<ChuckDataMessage> function) throws IOException {

        ChuckDataMessage dataMessage = new ChuckDataMessage(tid, relativePath);
        dataMessage.setStartPostion(preLen)
                .setTotalSize(length)
                .setData(data);
        function.apply(dataMessage);

    }

    //TODO 转移的文件无法打开，尤其视频文件
    public synchronized void decode(String basePath, ChuckDataMessage dataMessage) throws IOException {
        String fullPath = FileUtils.getFullPath(basePath, dataMessage.getPath());
        FileUtils.ensureParentPath(fullPath);
        File file = new File(fullPath);
        long curLen = 0;
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw")) {
            long startPostion = dataMessage.getStartPostion();
            byte[] data = dataMessage.getData();
            curLen = data.length;
            accessFile.seek(startPostion);
            accessFile.write(data);
            long totalSize = startPostion + curLen;
            if (totalSize == dataMessage.getTotalSize()) {
                logger.info("文件读取完成：{},{}", totalSize, dataMessage);
            }
        }
    }
}
