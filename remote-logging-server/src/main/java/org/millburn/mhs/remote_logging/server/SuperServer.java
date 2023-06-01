package org.millburn.mhs.remote_logging.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Describes a server that listens to the port and dispatch upcoming connections to smaller servers to handle them.
 *
 * @author Keming Fei
 */
public class SuperServer implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(SuperServer.class);
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ChannelFuture channelFuture;
    private final FileAppender faf;
    private final String desiredKey;

    /**
     * @param ip   an IPv4 without port
     * @param port a port
     */
    public SuperServer(String ip, int port, File logFileDirectory, String desiredKey) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();

        sb.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel sc) {
                        ChannelPipeline cp = sc.pipeline();
                        cp.addLast(new ChunkedWriteHandler());
                        cp.addLast(new MessageDecoder(), new Server(SuperServer.this.faf, SuperServer.this.desiredKey)
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        this.channelFuture = sb.bind(ip, port);
        this.faf = new FileAppender(logFileDirectory.getPath(), ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0), TimeUnit.DAYS.toMillis(1));
        this.desiredKey = desiredKey;
    }

    /**
     * The server waits and listens to the address
     */
    public void listen() {
        try {
            this.channelFuture.channel().closeFuture().sync();
        } catch(InterruptedException e) {
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
