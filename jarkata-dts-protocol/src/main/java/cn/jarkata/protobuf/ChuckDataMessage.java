package cn.jarkata.protobuf;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class ChuckDataMessage implements Serializable {
    public static final int CHUCK_SIZE = 2 * 1024;
    private String mac;
    private long tid;
    private String path;
    private byte[] data;
    private int startPostion;
    private int totalSize;

    public ChuckDataMessage(long tid, String path) {
        this.tid = tid;
        this.path = path;
    }

    public ChuckDataMessage() {
    }

    public int getStartPostion() {
        return startPostion;
    }

    public ChuckDataMessage setStartPostion(int startPostion) {
        this.startPostion = startPostion;
        return this;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public ChuckDataMessage setTotalSize(int totalSize) {
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

    public ByteBuf encode() {
        ByteBuf buffer = Unpooled.buffer(CHUCK_SIZE);
        buffer.writeInt('J');
        buffer.writeLong(tid);
        buffer.writeInt(startPostion);
        buffer.writeInt(totalSize);
        InnerMessage message = new InnerMessage();
        message.setPath(this.getPath());
        message.setData(this.getData());
        String data = JSON.toJSONString(message);
        byte[] messageBytes = data.getBytes(StandardCharsets.UTF_8);
        int dataLen = messageBytes.length;
        buffer.writeInt(dataLen);
        buffer.writeBytes(messageBytes);
        return buffer;
    }

    /**
     * 4+8+4+4+dataLen+data
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
        int startPostion = buf.getInt(batchIndex);
        chuckDataMessage.setStartPostion(startPostion);
        int totalIndex = batchIndex + 4;
        int totalSize = buf.getInt(totalIndex);
        chuckDataMessage.setTotalSize(totalSize);
        int dataLenIndex = totalIndex + 4;
        int dataLen = buf.getInt(dataLenIndex);
        buf.skipBytes(4 + 8 + 4 + 4 + 4);
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
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
