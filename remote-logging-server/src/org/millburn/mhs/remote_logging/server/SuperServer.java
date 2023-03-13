package org.millburn.mhs.remote_logging.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.Closeable;

/**
 * Describes a server that listens to the port and dispatch upcoming connections to smaller servers to handle them.
 *
 * @author Keming Fei
 */
public class SuperServer implements Closeable {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ChannelFuture channelFuture;

    /**
     * @param ip an IPv4 address, cannot include ports
     * @param port the port
     */
    public SuperServer(String ip, int port) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel sc) {
                        sc.pipeline().addLast(new Server());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        this.channelFuture = sb.bind(ip, port);
    }

    /**
     * The server waits and listens to the address
     */
    public void listen() {
        try {
            this.channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the server
     */
    @Override
    public void close() {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
    }
}
