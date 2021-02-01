package cn.jarkata.dts.client;

import cn.jarkata.dts.client.connection.NettyConnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 */
public class DtsClient {


    public static void main(String[] args) throws Exception {
        AtomicLong count = new AtomicLong(0);
        NettyConnection connection = new NettyConnection("localhost", 8089);
        String msg = count.getAndIncrement() + "测试中国";
        try {
            String localhost = connection.write(msg);
            System.out.println(msg + "::" + localhost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
