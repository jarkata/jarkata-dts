package cn.jarkata.dts;


import cn.jarkata.dts.server.ShutdownServer;
import cn.jarkata.dts.server.TransferFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTSStarter {

    private static final Logger logger = LoggerFactory.getLogger(DTSStarter.class);

    public static void main(String[] args) {
        new TransferFileServer().start();
        new ShutdownServer().start();
        logger.info("server start success");
    }
}
