package org.millburn.mhs.remote_logging.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Code for testing
 * Some content will be added to the library, some will be discarded
 *
 * @author Keming Fei
 */
public class ClientTests {
    public static void main(String[] args) throws Exception {
        RemoteLogger rl = new RemoteLogger("localhost", 6969, "logger");
        /*rl.addOnConnectListener(rl1 -> {
            PrintStream ps = rl1.getAsPrintStream();
            System.setOut(ps);
            System.setErr(ps);
        });*/
        rl.attemptToConnect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = "";
        while(!text.equals("exit")) {
            System.out.print("Enter your text: ");
            text = reader.readLine();
            byte[] bytes = (text + "\n").getBytes();
            System.out.println("String Byte Length: " + bytes.length);
            rl.log(text);
        }

        rl.close();
    }
}

