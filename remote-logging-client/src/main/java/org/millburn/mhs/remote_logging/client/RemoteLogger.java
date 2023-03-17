package org.millburn.mhs.remote_logging.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ScheduledFuture;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class RemoteLogger implements Closeable {
    private final EventLoopGroup eventLoopGroup;
    private final String ip;
    private final int port;
    private String name;
    private final Bootstrap b;
    private RemoteOutputStream ros;
    private volatile boolean connected;
    private volatile ScheduledFuture<?> reconnectFuture;

    public RemoteLogger(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.connected = false;

        this.eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("EventLoopGroupThreadCreator", true));

        b = new Bootstrap();
        b.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new Handler(RemoteLogger.this));
                    }
                });

        this.attemptToReconnect();
//        this.connect();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
        this.ros.write(MessageType.SPECIFY_NAME);
        this.ros.writeString(this.name);
        this.ros.flush();
    }

    void setConnected(boolean v) {
        this.connected = v;
    }

    private void connect() {
        if(this.connected) {
            this.reconnectFuture.cancel(true);
            return;
        }

        ChannelFuture cf = this.b.connect(this.ip, this.port);
        this.ros = new RemoteOutputStream(cf.channel());
        System.out.println("Retrying Connection!");
        if(this.connected) {
            this.reconnectFuture.cancel(true);
        }
    }

    public void attemptToReconnect() {
        if(this.connected) {
            return;
        }

        this.reconnectFuture = this.eventLoopGroup.scheduleWithFixedDelay(this::connect, 0, 1, TimeUnit.SECONDS);
    }

    public void log(String message) {
        this.ros.write(MessageType.LOG);
        this.ros.writeString(message);
        this.ros.flush();
    }

    public void log(Object x) {
        this.log(x.toString());
    }

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

    public PrintStream getAsPrintStream() {
        return new RemoteLoggerPrintStream();
    }

    @Override
    public void close() {
        this.eventLoopGroup.shutdownGracefully();
    }
}