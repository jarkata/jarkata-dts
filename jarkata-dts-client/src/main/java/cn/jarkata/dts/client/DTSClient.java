package cn.jarkata.dts.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class DTSClient {

    private static final Logger logger = LoggerFactory.getLogger(DTSClient.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            NettyClient.transfer(args);
        } finally {
            long dur = System.currentTimeMillis() - start;
            logger.info("耗时:{}ms", dur);
        }
    }
}
