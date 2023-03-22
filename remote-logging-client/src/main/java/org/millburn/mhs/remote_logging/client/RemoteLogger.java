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
import java.util.concurrent.TimeUnit;

public class RemoteLogger implements Closeable {
    private final EventLoopGroup eventLoopGroup;
    private final String ip;
    private final int port;
    private final Bootstrap b;
    private String name;
    private RemoteOutputStream ros;

    public RemoteLogger(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;

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

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
        this.ros.write(MessageType.SPECIFY_NAME);
        this.ros.writeString(this.name);
        this.ros.flush();
    }

    private final ChannelFutureListener cfl = new ChannelFutureListener() {
        private void addCloseDetectListener(Channel channel) {
            channel.closeFuture().addListener((ChannelFutureListener)future -> {
                System.out.println("Connection lost.");
                RemoteLogger.this.eventLoopGroup.schedule(RemoteLogger.this::connect, 1, TimeUnit.SECONDS);
            });
        }

        @Override
        public void operationComplete(ChannelFuture future) {
            if(!future.isSuccess()) {//if is not successful, reconnect
                future.channel().close();
                System.out.println("Attempting to reconnect...");
                RemoteLogger.this.eventLoopGroup.schedule(() -> {
                    RemoteLogger.this.b.connect(RemoteLogger.this.ip, RemoteLogger.this.port).addListener(RemoteLogger.this.cfl);
                }, 1, TimeUnit.SECONDS);
                return;
            }

            Channel c = future.channel();
            System.out.println("Connection established, creating RemoteOutputStream...");
            RemoteLogger.this.ros = new RemoteOutputStream(c);
            addCloseDetectListener(c);
        }
    };

    public void connect() {
        ChannelFuture f = this.b.connect(this.ip, this.port);
        f.addListener(this.cfl);
    }

    public void attemptToConnect() {
        this.eventLoopGroup.execute(this::connect);
    }

    public void log(String message) {
        this.ros.write(MessageType.LOG);
        this.ros.writeString(message);
        this.ros.flush();
    }

    public void log(Object x) {
        this.log(x.toString());
    }

    public PrintStream getAsPrintStream() {
        return new RemoteLoggerPrintStream();
    }

    @Override
    public void close() {
        this.eventLoopGroup.shutdownGracefully();
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
}