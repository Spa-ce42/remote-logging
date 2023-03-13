package org.millburn.mhs.remote_logging.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;

/**
 * Code for testing
 * Some content will be added to the library, some will be discarded
 *
 * @author Keming Fei
 */
public class ClientTests {
    public static RemoteLogger rl;
    public static void main(String[] args) throws Exception {
        rl = new RemoteLogger("localhost", 8080, "logger");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = "";
        while (!text.equals("exit")) {
            System.out.print("Enter your text: ");
            text = reader.readLine();
            byte[] bytes = (text + "\n").getBytes();
            System.out.println("String Byte Length: " + bytes.length);
            rl.log(text);
        }
        rl.close();
    }
}

