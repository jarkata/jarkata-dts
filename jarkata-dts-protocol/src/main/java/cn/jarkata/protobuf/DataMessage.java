package cn.jarkata.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class DataMessage implements Serializable {
    private final long tid;
    private final String path;
    private final int size;
    private byte[] data;

    public DataMessage(long tid, String path, int size) {
        this.tid = tid;
        this.path = path;
        this.size = size;
    }

    public DataMessage(String path, byte[] data) {
        this(0, path, data);
    }

    public DataMessage(long tid, String path, byte[] data) {
        this.tid = tid;
        this.path = path;
        this.data = data;
        this.size = data.length;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public int getPathLen() {
        return path.getBytes(StandardCharsets.UTF_8).length;
    }

    public long getTid() {
        return tid;
    }


    public ByteBuf encode() {
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(8 * 1024);
        byte[] macBytes = "JF".getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(macBytes.length);
        buffer.writeBytes(macBytes);
        buffer.writeLong(tid);
        byte[] pathBytes = path.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(pathBytes.length);
        buffer.writeBytes(pathBytes);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
        return buffer;
    }

}
