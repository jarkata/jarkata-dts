package cn.jarkata.dts;


import cn.jarkata.dts.connection.NettyServer;

public class DTSStarter {

    public static void main(String[] args) {
        new NettyServer(8080).start();
        System.out.println("starting-----end");
    }
}
