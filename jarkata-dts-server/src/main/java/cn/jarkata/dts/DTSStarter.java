package cn.jarkata.dts;


import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.constant.JarkataConstant;
import cn.jarkata.dts.server.NettyServer;

import static cn.jarkata.dts.constant.JarkataConstant.*;

public class DTSStarter {

    public static void main(String[] args) {
        String serverPort = Env.getProperty(SERVER_PORT, SERVER_PORT_DEFUALT_VAL);
        int port = Integer.parseInt(serverPort);
        new NettyServer(port).start();
        System.out.println("starting-----end");
    }
}
