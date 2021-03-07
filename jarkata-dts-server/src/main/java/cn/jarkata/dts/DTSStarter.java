package cn.jarkata.dts;


import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT_DEFAULT_VAL;

public class DTSStarter {
    private static final Logger logger = LoggerFactory.getLogger(DTSStarter.class);

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        if (Objects.nonNull(args) && args.length > 0 && "shutdown".equalsIgnoreCase(args[0])) {
            System.out.println("系统即将关闭");
            System.exit(0);
            return;
        }
        String serverPort = Env.getProperty(SERVER_PORT, SERVER_PORT_DEFAULT_VAL);
        int port = Integer.parseInt(serverPort);
        new NettyServer(port).start();
        logger.info("server start success");
    }
}
