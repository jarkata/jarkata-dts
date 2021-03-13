package cn.jarkata.protobuf;

public class InnerMessage {
    private String path;
    private byte[] data;

    public String getPath() {
        return path;
    }

    public InnerMessage setPath(String path) {
        this.path = path;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public InnerMessage setData(byte[] data) {
        this.data = data;
        return this;
    }
}
