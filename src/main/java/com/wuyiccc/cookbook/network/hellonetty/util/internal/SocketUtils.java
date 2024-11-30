package com.wuyiccc.cookbook.network.hellonetty.util.internal;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

/**
 * @author wuyiccc
 * @date 2024/11/30 15:12
 * <p>
 * 为什么要用: AccessController.doPrivileged
 * 参考如下netty issue: https://github.com/netty/netty/pull/6146
 * 简而言之, 就是为了在一些禁用了java网络权限的应用环境下，又使用了netty, 那么需要单独对netty的jar开启网络权限, 这样
 * 第三方软件使用netty的时候, 通过AccessController.doPrivileged让这些三方软件调用netty不会报无权限的异常,而这些三方软件自己调用网络权限的时候仍然会被禁止
 */
public class SocketUtils {

    private SocketUtils() {
    }

    public static void connect(final Socket socket, final SocketAddress remoteAddress, final int timeout) throws IOException {

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    socket.connect(remoteAddress, timeout);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static void bind(final Socket socket, final SocketAddress bindpoint) throws IOException {

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() {
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static boolean connect(final SocketChannel socketChannel, final SocketAddress remoteAddress) throws IOException {


        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws Exception {

                    return socketChannel.connect(remoteAddress);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static void bind(final SocketChannel socketChannel, final SocketAddress address) throws IOException {

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    socketChannel.bind(address);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {

        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
                @Override
                public SocketChannel run() throws Exception {

                    return serverSocketChannel.accept();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }


    public static void bind(final DatagramChannel networkChannel, final SocketAddress address) throws IOException {

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {

                    networkChannel.bind(address);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static SocketAddress localSocketAddress(final ServerSocket socket) {
        return AccessController.doPrivileged(new PrivilegedAction<SocketAddress>() {
            @Override
            public SocketAddress run() {

                return socket.getLocalSocketAddress();
            }
        });
    }

    public static InetAddress addressByName(final String hostname) throws UnknownHostException {

        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
                @Override
                public InetAddress run() throws Exception {

                    return InetAddress.getByName(hostname);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (UnknownHostException) e.getCause();
        }
    }

    public static InetAddress[] allAddressesByName(final String hostname) throws UnknownHostException {

        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress[]>() {
                @Override
                public InetAddress[] run() throws Exception {

                    return InetAddress.getAllByName(hostname);
                }
            });
        } catch (PrivilegedActionException e) {

            throw (UnknownHostException) e.getCause();
        }
    }

    public static InetSocketAddress socketAddress(final String hostname, final int port) {

        return AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
            @Override
            public InetSocketAddress run() {

                return new InetSocketAddress(hostname, port);
            }
        });
    }


    public static Enumeration<InetAddress> addressFromNetworkInterface(final NetworkInterface intf) {

        return AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>() {
            @Override
            public Enumeration<InetAddress> run() {

                return intf.getInetAddresses();
            }
        });
    }


    public static byte[] hardwareAddressFromNetworkInterface(final NetworkInterface intf) throws SocketException {

        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
                @Override
                public byte[] run() throws Exception {

                    return intf.getHardwareAddress();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (SocketException) e.getCause();
        }
    }

}
