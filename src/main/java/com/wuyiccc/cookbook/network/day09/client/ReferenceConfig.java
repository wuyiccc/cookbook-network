package com.wuyiccc.cookbook.network.day09.client;

/**
 * @author wuyiccc
 * @date 2024/11/15 22:57
 */
public class ReferenceConfig {


    public static final long DEFAULT_TIMEOUT = 5000;

    public static final String DEFAULT_SERVER_HOST = "127.0.0.1";

    public static final int DEFAULT_SERVER_PORT = 8998;

    private Class serverInterfaceClass;


    public ReferenceConfig(Class serverInterfaceClass) {
        this.serverInterfaceClass = serverInterfaceClass;
    }

    public Class getServerInterfaceClass() {
        return serverInterfaceClass;
    }

    public void setServerInterfaceClass(Class serverInterfaceClass) {
        this.serverInterfaceClass = serverInterfaceClass;
    }


}
