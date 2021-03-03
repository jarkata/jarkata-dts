package cn.jarkata.common.protobuf;

import cn.jarkata.common.serializer.FileMessage;

public class MessageWrapper {

    private final FileMessage data;

    public MessageWrapper(FileMessage data) {
        this.data = data;
    }

    public FileMessage getData() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
