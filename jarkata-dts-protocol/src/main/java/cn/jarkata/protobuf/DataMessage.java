package cn.jarkata.protobuf;

import java.io.Serializable;

public class DataMessage implements Serializable {
    private final long tid;
    private final String path;
    private final long size;
    private final byte[] data;

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

    public long getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public long getTid() {
        return tid;
    }
}
