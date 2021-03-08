package cn.jarkata.dts.config;

import cn.jarkata.dts.common.Env;

import static cn.jarkata.dts.common.constant.JarkataConstant.*;

public class ServerConfig {

    public int getBossThreadCount() {
        return Env.getIntProperty("boss.thread.count", 0);
    }

    public int getWorkThreadCount() {
        return Env.getIntProperty(SERVER_WORK_THREADS, SERVER_WORK_THREADS_DEFAULT_VAL);
    }

    public int getIoThreadCount() {
        return Env.getIntProperty(SERVER_IO_THREADS, SERVER_IO_THREADS_DEFAULT_VAL);
    }

    public Integer getServerPort() {
        return Env.getIntProperty(SERVER_PORT, SERVER_PORT_DEFAULT_VAL);
    }

    public int getShutdownPort() {
        return Env.getIntProperty("shutdown.port", 20889);
    }
}
