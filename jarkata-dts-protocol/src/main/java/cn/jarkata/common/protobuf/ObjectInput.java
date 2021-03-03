package cn.jarkata.common.protobuf;

import cn.jarkata.common.serializer.FileMessage;
import io.protostuff.GraphIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ObjectInput {

    private final DataInputStream dis;

    public ObjectInput(InputStream dis) {
        this.dis = new DataInputStream(dis);
    }

    public FileMessage readObject() throws IOException {
        int bytesLength = dis.readInt();
        if (bytesLength < 0) {
            throw new IOException();
        }
        byte[] bytes = new byte[bytesLength];
        dis.readFully(bytes, 0, bytesLength);

        Schema<MessageWrapper> schema = RuntimeSchema.getSchema(MessageWrapper.class);
        MessageWrapper messageWrapper = schema.newMessage();
        System.out.println(messageWrapper);
        GraphIOUtil.mergeFrom(bytes, messageWrapper, schema);
        return messageWrapper.getData();
    }
}
