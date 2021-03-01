package cn.jarkata.dts.client.channel;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseFuture {
    private static final ConcurrentHashMap<String, String> responseFuture = new ConcurrentHashMap<>();

    public void setResponse(String rpid, String message) {
        responseFuture.put(rpid, message);
    }

    public String getResponse(String rpid, long timeout) {
        return responseFuture.get(rpid);
    }
}
