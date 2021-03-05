package cn.jarkata.protobuf.utils;

import cn.jarkata.protobuf.Wrapper;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufUtils {

    private static final Map<Class, Schema<Wrapper>> cache = new ConcurrentHashMap<>();

    /**
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            Wrapper wrapper = new Wrapper(obj);
            Schema<Wrapper> schema = getScheme();
            return GraphIOUtil.toByteArray(wrapper, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    public static Object readObject(byte[] data) {
        Schema<Wrapper> schema = getScheme();
        Wrapper message = schema.newMessage();
        GraphIOUtil.mergeFrom(data, message, schema);
        return message.getData();
    }

    private static Schema<Wrapper> getScheme() {
        Schema<Wrapper> schema = cache.get(Wrapper.class);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.getSchema(Wrapper.class);
            cache.putIfAbsent(Wrapper.class, schema);
        }
        return schema;
    }

}
