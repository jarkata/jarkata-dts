package cn.jarkata.common.protobuf;

import cn.jarkata.common.serializer.FileMessage;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ObjectOutput {

    private final LinkedBuffer buffer = LinkedBuffer.allocate();
    private final DataOutputStream dos;

    public ObjectOutput(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
    }

    public void write(FileMessage obj) throws IOException {
        byte[] bytes;
        try {
            MessageWrapper wrapper = new MessageWrapper(obj);
            Schema<MessageWrapper> schema = RuntimeSchema.getSchema(MessageWrapper.class);
            bytes = GraphIOUtil.toByteArray(wrapper, schema, buffer);
        } finally {
            buffer.clear();
        }
        dos.writeInt(bytes.length);
        dos.write(bytes);
    }


    public void flushBuffer() throws IOException {
        dos.flush();
    }
}
