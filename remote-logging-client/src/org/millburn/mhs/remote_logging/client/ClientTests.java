package org.millburn.mhs.remote_logging.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.PrintStream;

/**
 * Code for testing
 * Some content will be added to the library, some will be discarded
 *
 * @author Keming Fei
 */
public class ClientTests {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel sc) {
                        sc.pipeline().addLast(new Handler());
                    }
                });

        ChannelFuture f = b.connect("localhost", 8080).sync();

        System.setOut(new PrintStream(new RemoteOutputStream(f.channel())));

        for(int i = 0; i < 100; ++i) {
            System.out.println(i);
        }

        System.out.flush();

        group.shutdownGracefully();
    }
}
