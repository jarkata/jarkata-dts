package cn.jarkata.dts;


import cn.jarkata.dts.server.ShutdownServer;
import cn.jarkata.dts.server.TransferFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTSStarter {

    private static final Logger logger = LoggerFactory.getLogger(DTSStarter.class);

    public static void main(String[] args) throws InterruptedException {
        new TransferFileServer().start();
        new ShutdownServer().start();
//        MonitorService monitorService = new MonitorService();
//        monitorService.report();
        logger.info("server start success ");
    }
}
