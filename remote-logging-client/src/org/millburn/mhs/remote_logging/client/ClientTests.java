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
    public static void main(String[] args) throws Exception {
        RemoteLogger rl = new RemoteLogger("localhost", 8080, "logger");
        rl.log("hello world!");
        rl.close();

//        System.setOut(new PrintStream(ros)); REMOVED THIS LINE, IDK WHAT IT DID BUT NOW IT WORKS


        /*for(int i = 0; i < 100; ++i) {
            System.out.println(i);
        }

        System.out.flush();*/

        //Currently, the code does not work since Netty breaks large byte arrays into smaller ones.

        // Alex Added this for easier testing
        /*BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = "";
        while (!text.equals("exit")) {
            System.out.print("Enter your text: ");
            text = reader.readLine();
            System.out.println("This is the text: " + text);
            byte[] bytes = (text + "\n").getBytes();
            System.out.println("String Byte Length: " + bytes.length);
            ros.write(bytes);
            ros.flush();
        }*/
//        Random r = new Random();
//        byte[] c = new byte[1000];
//        r.nextBytes(c);
//        ros.write(c);
//        ros.flush();
    }
}

