package cn.jarkata.dts;


import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.server.NettyServer;

import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT;
import static cn.jarkata.dts.common.constant.JarkataConstant.SERVER_PORT_DEFAULT_VAL;

public class DTSStarter {

    public static void main(String[] args) {
        String serverPort = Env.getProperty(SERVER_PORT, SERVER_PORT_DEFAULT_VAL);
        int port = Integer.parseInt(serverPort);
        new NettyServer(port).start();
        System.out.println("starting-----end");
    }
}
