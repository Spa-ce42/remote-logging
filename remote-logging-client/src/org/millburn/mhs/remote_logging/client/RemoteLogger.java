package org.millburn.mhs.remote_logging.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;

public class RemoteLogger implements Closeable {
    private final EventLoopGroup eventLoopGroup;
    private final ChannelFuture cf;
    private final RemoteOutputStream ros;

    public RemoteLogger(String ip, int port, String name) {
        this.eventLoopGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new Handler());
                    }
                });

        this.cf = b.connect(ip, port);
        this.ros = new RemoteOutputStream(this.cf.channel());
    }

    @Override
    public void close() throws IOException {
        this.eventLoopGroup.shutdownGracefully();
    }
}
