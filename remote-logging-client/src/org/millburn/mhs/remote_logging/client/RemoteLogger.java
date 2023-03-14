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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RemoteLogger implements Closeable {
    private final EventLoopGroup eventLoopGroup;
    private final String ip;
    private final int port;
    private final String name;
    private final Bootstrap b;
    private RemoteOutputStream ros;
    private ScheduledFuture<?> reconnectFuture;

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
                });

        attemptToReconnect();
    }

    public String getName() {
        return this.name;
    }

    private void connect() {
        ChannelFuture cf = this.b.connect(this.ip, this.port);
        this.ros = new RemoteOutputStream(cf.channel());
        this.ros.write(MessageType.SPECIFY_NAME);
        this.ros.writeString(name);
        this.ros.flush();

        if (cf.isSuccess() && this.reconnectFuture != null) {
            this.reconnectFuture.cancel(true);
        }
    }

    public void attemptToReconnect() {
        // THE GOAL IS TO GET THIS FUNCTION TO WAIT UNTIL THE CONNECTION IS COMPLETELY ESTABLISHED
        // CURRENTLY IT DOES NOT WORK

//        System.out.println("RNNIGN");
        this.reconnectFuture = this.eventLoopGroup.scheduleAtFixedRate(this::connect, 0, 1, TimeUnit.SECONDS);

        /*while(true) {
            this.cf = this.b.connect(this.ip, this.port);
            cf.awaitUninterruptibly();

            assert cf.isDone();

            if(cf.isCancelled()) {
                System.out.println("Connection Cancelled.");
            } else if(!cf.isSuccess()) {
                cf.cause().printStackTrace();
            } else {
                System.out.println("Connected");
                break;
            }
        }*/
//        cf.awaitUninterruptibly();
//        System.out.println(cf.channel().isActive());
//        try {
//            this.cf = this.b.connect(this.ip, this.port).syncUninterruptibly();
//        } catch (InterruptedException e) {
//            System.out.println("Wait was interrupted.");
//        } catch (ConnectException e) {
//            System.out.println("Connection refused");
//        }

//        while (!cf.channel().isActive()) {
//            try {
//                System.out.println("Waiting for connection.");
//                Thread.sleep(1000);
//                cf.channel().close();
//                cf.awaitUninterruptibly();
//                this.cf = this.b.connect(this.ip, this.port);
//                System.out.println(cf.isDone());
//                System.out.println(cf.isSuccess());
////                System.out.println(cf.channel().isOpen());
//            } catch (InterruptedException e) {
//                System.out.println("Wait was interrupted.");
//            }
//        }
//        System.out.println("Connected");
//
//        this.ros = new RemoteOutputStream(this.cf.channel());

//        while (!cf.channel().isOpen()) {
//            System.out.println("Connection failed, retrying!");
//
//            this.ros = new RemoteOutputStream(this.cf.channel());
//
//        }
    }

    public void log(String message) {
        this.ros.write(MessageType.LOG);
        this.ros.writeString(message);
        this.ros.flush();
    }

    @Override
    public void close() throws IOException {
        this.eventLoopGroup.shutdownGracefully();
    }
}
