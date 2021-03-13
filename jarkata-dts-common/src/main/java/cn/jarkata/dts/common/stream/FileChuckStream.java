package cn.jarkata.dts.common.stream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChuckStream implements Closeable {
    private final long length;
    private final File file;
    private final ByteBuffer buffer;
    private final FileInputStream fileInputStream;

    public FileChuckStream(File file, int readLength) throws FileNotFoundException {
        this.file = file;
        this.fileInputStream = new FileInputStream(file);
        this.length = file.length();
        this.buffer = ByteBuffer.allocate(readLength);
    }

    public byte[] read() throws IOException {
        buffer.clear();
        FileChannel fileChannel = fileInputStream.getChannel();
        int len = fileChannel.read(buffer);
        if (len == -1) {
            return null;
        }
        try {
            byte[] data = new byte[len];
            buffer.flip();
            buffer.get(data);
            return data;
        } catch (Throwable ex) {
            System.err.println(len);
            ex.printStackTrace();
            return null;
        } finally {
            buffer.clear();
        }
    }

    @Override
    public void close() throws IOException {
        fileInputStream.close();
    }

    public long getLength() {
        return length;
    }

    public File getFile() {
        return file;
    }

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }
}
