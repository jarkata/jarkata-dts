package cn.jarkata.dts;


import cn.jarkata.dts.common.Env;
import cn.jarkata.dts.server.NettyServer;

public class DTSStarter {

    public static void main(String[] args) {
        String serverPort = Env.getProperty("server.port", "8089");
        int port = Integer.parseInt(serverPort);
        new NettyServer(port).start();
        System.out.println("starting-----end");
    }
}
