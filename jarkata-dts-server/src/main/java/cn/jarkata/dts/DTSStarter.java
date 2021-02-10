package cn.jarkata.dts;


import cn.jarkata.dts.server.NettyServer;

public class DTSStarter {

    public static void main(String[] args) {
        String serverPort = System.getProperty("server.port", "8089");
        int port = Integer.parseInt(serverPort);
        new NettyServer(port).start();
        System.out.println("starting-----end");
    }
}
