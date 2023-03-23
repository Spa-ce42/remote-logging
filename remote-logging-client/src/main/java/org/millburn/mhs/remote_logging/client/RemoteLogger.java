package org.millburn.mhs.remote_logging.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Describes a logger that logs content to a remotely connected server instead of locally
 * @author Keming Fei, Alex Kolodkin
 */
public class RemoteLogger implements Closeable {
    private final EventLoopGroup eventLoopGroup;
    private final String ip;
    private final int port;
    private final Bootstrap b;
    private String name;
    private RemoteOutputStream ros;
    List<OnConnectedListener> onConnectedListeners;

    /**
     * @param ip the server's ip address
     * @param port the server's port
     * @param name the name of the logger
     */
    public RemoteLogger(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.onConnectedListeners = new ArrayList<>();

        this.eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("EventLoopGroupThreadCreator", true));

        b = new Bootstrap();
        b.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new Handler(RemoteLogger.this));
                    }
                })
        ;
    }

    public void addOnConnectListener(OnConnectedListener ocl) {
        this.onConnectedListeners.add(ocl);
    }

    public void removeOnConnectListener(OnConnectedListener ocl) {
        this.onConnectedListeners.remove(ocl);
    }

    /**
     * @return the name of the logger
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the logger, changes it remotely if a connection is present
     * @param s the new name of the logger
     */
    public void setName(String s) {
        this.name = s;
        this.ros.write(MessageType.SPECIFY_NAME);
        this.ros.writeString(this.name);
        this.ros.flush();
    }

    private void listenToCloseFuture(ChannelFuture future) {
        System.out.println("Connection lost.");
        RemoteLogger.this.eventLoopGroup.schedule(RemoteLogger.this::connect, 1, TimeUnit.SECONDS);
    }

    private final ChannelFutureListener closeFutureListener = this::listenToCloseFuture;

    private void listenToConnectFuture(ChannelFuture future) {
        //If the connection was not successful, reconnect
        if(!future.isSuccess()) {
            future.channel().close();
            System.out.println("Attempting to reconnect...");
            //Reconnect, passes cfl to handle the results
            this.eventLoopGroup.schedule(() -> {
                this.b.connect(this.ip, this.port).addListener(this.cfl);
            }, 1, TimeUnit.SECONDS);
            return;
        }

        //If successful, initialize ros, adds a close future listener in case the channel closes
        Channel c = future.channel();
        System.out.println("Connection established, creating RemoteOutputStream...");
        RemoteLogger.this.ros = new RemoteOutputStream(c);
        c.closeFuture().addListener(RemoteLogger.this.closeFutureListener);
    }

    private final ChannelFutureListener cfl = this::listenToConnectFuture;

    /**
     * Tries to connect to the server, if the connection is refused, it attempts again in a short period of time
     */
    public void connect() {
        ChannelFuture f = this.b.connect(this.ip, this.port);
        f.addListener(this.cfl);
    }

    /**
     * In a separate thread, tries to connect to the server, if the connection is refused, it attempts again in a short period of time
     */
    public void attemptToConnect() {
        this.eventLoopGroup.execute(this::connect);
    }

    /**
     * Logs a string
     * @param message a string
     */
    public void log(String message) {
        this.ros.write(MessageType.LOG);
        this.ros.writeString(message);
        this.ros.flush();
    }

    /**
     * Logs the string value of an object
     * @param x an object
     */
    public void log(Object x) {
        this.log(x.toString());
    }

    /**
     * @return the remote logger as a PrintStream
     */
    public PrintStream getAsPrintStream() {
        return new RemoteLoggerPrintStream();
    }

    /**
     * Closes the logger and the connection associated with it
     */
    @Override
    public void close() {
        this.eventLoopGroup.shutdownGracefully();
    }

    //TODO: Implement all of PrintStream's methods
    private class RemoteLoggerPrintStream extends PrintStream {
        public RemoteLoggerPrintStream(OutputStream out) {
            super(out);
        }

        public RemoteLoggerPrintStream() {
            this(RemoteLogger.this.ros);
        }

        @Override
        public void println() {
            RemoteLogger.this.log("");
        }

        @Override
        public void println(boolean x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(char x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(int x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(long x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(float x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(double x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(char[] x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(String x) {
            RemoteLogger.this.log(x);
        }

        @Override
        public void println(Object x) {
            RemoteLogger.this.log(x);
        }
    }
}