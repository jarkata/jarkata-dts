package cn.jarkata.protobuf;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.io.Closeable;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class ChuckDataMessage implements Serializable, Closeable {
    public static final int CHUCK_SIZE = 1024 * 1024;
    private String mac;
    private long tid;
    private String path;
    private byte[] data;
    private long startPostion;
    private long totalSize;

    private static final ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(CHUCK_SIZE * 2);

    public ChuckDataMessage(long tid, String path) {
        this.tid = tid;
        this.path = path;
    }

    public ChuckDataMessage() {
    }

    public long getStartPostion() {
        return startPostion;
    }

    public ChuckDataMessage setStartPostion(long startPostion) {
        this.startPostion = startPostion;
        return this;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public ChuckDataMessage setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public String getMac() {
        return mac;
    }

    public ChuckDataMessage setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public ChuckDataMessage setTid(long tid) {
        this.tid = tid;
        return this;
    }

    public ChuckDataMessage setPath(String path) {
        this.path = path;
        return this;
    }

    public long getTid() {
        return tid;
    }

    public String getPath() {
        return path;
    }

    public byte[] getData() {
        return data;
    }

    public ChuckDataMessage setData(byte[] data) {
        this.data = data;
        return this;
    }

    /**
     * 4+8+8+8+4+messageLen
     *
     * @return
     */
    public ByteBuf encode() {
        if (totalSize <= 0) {
            throw new IllegalArgumentException("invalid totalSize=" + totalSize);
        }
        ByteBuf byteBuf = Unpooled.buffer(CHUCK_SIZE * 2);
        byteBuf.writeInt('J');
        byteBuf.writeLong(tid);
        byteBuf.writeLong(startPostion);
        byteBuf.writeLong(totalSize);
        InnerMessage message = new InnerMessage();
        message.setPath(this.getPath());
        message.setData(this.getData());
        String data = JSON.toJSONString(message);
        byte[] messageBytes = data.getBytes(StandardCharsets.UTF_8);
        int dataLen = messageBytes.length;
        byteBuf.writeInt(dataLen);
        byteBuf.writeBytes(messageBytes);
        return byteBuf;
    }

    /**
     * 4+8+8+8+4+data
     *
     * @param buf
     * @return
     */
    public static ChuckDataMessage decode(ByteBuf buf) {
        ChuckDataMessage chuckDataMessage = new ChuckDataMessage();
        int macLen = buf.getInt(0);
        chuckDataMessage.setMac(String.valueOf((char) macLen));
        int tidIndex = 4;
        // tid
        long tid = buf.getLong(tidIndex);
        chuckDataMessage.setTid(tid);
        int batchIndex = tidIndex + 8;
        long startPostion = buf.getLong(batchIndex);
        if (startPostion < 0) {
            throw new IllegalArgumentException("pos=" + startPostion + ",posIndex=" + batchIndex);
        }
        chuckDataMessage.setStartPostion(startPostion);
        int totalIndex = batchIndex + 8;
        long totalSize = buf.getLong(totalIndex);
        chuckDataMessage.setTotalSize(totalSize);
        if (totalSize <= 0) {
            throw new IllegalArgumentException("pos=" + totalSize + ",totalIndex=" + totalIndex);
        }
        int dataLenIndex = totalIndex + 8;
        int dataLen = buf.getInt(dataLenIndex);
        buf.skipBytes(4 + 8 + 8 + 8 + 4);
        byte[] bodyData = new byte[dataLen];
        buf.readBytes(bodyData);
        String messageJson = new String(bodyData, StandardCharsets.UTF_8);
        InnerMessage message = JSON.parseObject(messageJson, InnerMessage.class);
        chuckDataMessage.setPath(message.getPath());
        chuckDataMessage.setData(message.getData());
        return chuckDataMessage;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChuckDataMessage{");
        sb.append("mac='").append(mac).append('\'');
        sb.append(", tid=").append(tid);
        sb.append(", path='").append(path).append('\'');
        sb.append(", startPostion=").append(startPostion);
        sb.append(", totalSize=").append(totalSize);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void close() {
//        buffer.clear();
    }
}
