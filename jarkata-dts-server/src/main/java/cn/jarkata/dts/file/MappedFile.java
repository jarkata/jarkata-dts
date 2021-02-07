package cn.jarkata.dts.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class MappedFile {
    private String fileName;
    private long fileSize;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;

    public MappedFile(String fileName, long fileSize) {
        try {
            init(fileName, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(String fileName, long fileSize) throws IOException {
        File file = new File(fileName);
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
    }

    public void put(String data) {
        this.mappedByteBuffer.put(data.getBytes(StandardCharsets.UTF_8));
    }

    public void flush() {
        mappedByteBuffer.force();
    }
}
