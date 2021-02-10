package cn.jarkata.dts.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class MappedFile {
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
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
    }

    public void put(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        this.mappedByteBuffer.putInt(bytes.length);
        this.mappedByteBuffer.put(bytes);
        this.mappedByteBuffer.putChar('#');
    }

    public void flush() {
        mappedByteBuffer.force();
    }

    public void getMessage() {
        int anInt = mappedByteBuffer.getInt(0);
        mappedByteBuffer.position(4);
        ByteBuffer slice = mappedByteBuffer.slice();
        byte[] dst = new byte[anInt + 2];
        slice.get(dst);
        System.out.println(anInt);
        System.out.println(new String(dst));
    }

    public void len() {
        int limit = this.mappedByteBuffer.limit() / 1024 / 1024;
        int position = this.mappedByteBuffer.position();
        System.out.println(limit + "+++++" + position);

    }

    public String get(int position, int len) {
        mappedByteBuffer.position(position);
        ByteBuffer slice = mappedByteBuffer.slice();
        byte[] bytes = new byte[len];
        slice.get(bytes);
        return new String(bytes);
    }
}
