package cn.jarkata.common.serializer;

import java.io.*;

public class FileMessage implements Serializable {

    private final String filename;
    private final String fileExt;

    private final byte[] stream;

    public FileMessage(String filename, String fileExt, InputStream stream) throws IOException {
        this.filename = filename;
        this.fileExt = fileExt;
        BufferedInputStream bis = new BufferedInputStream(stream);
        byte[] dist = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        while ((len = bis.read(dist)) != -1) {
            bos.write(dist, 0, len);
        }
        this.stream = bos.toByteArray();
    }

    public String getFilename() {
        return filename;
    }

    public String getFileExt() {
        return fileExt;
    }

    public byte[] getStream() {
        return stream;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileMessage{");
        sb.append("filename='").append(filename).append('\'');
        sb.append(", fileExt='").append(fileExt).append('\'');
        sb.append(", stream=").append(stream);
        sb.append('}');
        return sb.toString();
    }
}
