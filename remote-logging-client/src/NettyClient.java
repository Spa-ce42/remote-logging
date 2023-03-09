import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class NettyClient {
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
        RemotePrintStream rps = new RemotePrintStream(new RemoteOutputStream(f.channel()));
        System.setOut(rps);

        System.out.println("Hello world!");
        System.out.close();

        f.channel().closeFuture().sync();
        group.shutdownGracefully();
    }
}
