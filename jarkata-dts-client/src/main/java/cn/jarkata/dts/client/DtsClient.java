package cn.jarkata.dts.client;

import cn.jarkata.dts.client.channel.JarkataChannel;

import java.io.File;
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
            File file = new File("/Users/vkata/code/design/README.md");
            String localhost = connection.writeFile(file);
            System.out.println(msg + "::" + localhost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
