package cn.jarkata.dts.client;

import cn.jarkata.dts.client.connection.JarkataChannel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 */
public class DtsClient {


    public static void main(String[] args) throws Exception {
        AtomicLong count = new AtomicLong(0);
        JarkataChannel connection = new JarkataChannel("192.168.0.103", 20880);
        String msg = count.getAndIncrement() + "测试中国";
        try {
            String localhost = connection.write(msg);
            System.out.println(msg + "::" + localhost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
