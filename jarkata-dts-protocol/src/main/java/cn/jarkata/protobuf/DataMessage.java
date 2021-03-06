package cn.jarkata.protobuf;

import java.io.Serializable;

public class DataMessage implements Serializable {

    private final String path;
    private final long size;
    private final byte[] data;

    public DataMessage(String path, byte[] data) {
        this.path = path;
        this.data = data;
        this.size = this.data.length;
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
}
