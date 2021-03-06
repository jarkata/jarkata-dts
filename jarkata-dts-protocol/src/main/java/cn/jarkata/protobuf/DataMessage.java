package cn.jarkata.protobuf;

import java.io.*;

public class DataMessage implements Serializable {

    private final String path;
    private final long size;
    private final byte[] data;

    public DataMessage(String path, InputStream data) throws IOException {
        this.path = path;
        this.data = toByteArray(data);
        this.size = data.available();
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

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            byte[] dist = new byte[1024];

            int len;
            while ((len = bis.read(dist)) != -1) {
                bos.write(dist, 0, len);
            }
            return bos.toByteArray();
        }
    }
}
