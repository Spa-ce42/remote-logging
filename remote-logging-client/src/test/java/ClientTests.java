import org.millburn.mhs.remote_logging.client.RemoteLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

/**
 * Code for testing
 * Some content will be added to the library, some will be discarded
 *
 * @author Keming Fei
 */
public class ClientTests {
    public static void main(String[] args) {
        CountDownLatch cdl = new CountDownLatch(1);
        RemoteLogger rl = new RemoteLogger("localhost", 6969, "logger");
        rl.addOnConnectListener(rl1 -> {
            PrintStream ps = rl1.getAsPrintStream();
//            System.setOut(ps);
            System.setErr(ps);
            cdl.countDown();
        });
        rl.attemptToConnect();

        try {
            cdl.await();
        } catch(InterruptedException e) {
            e.printStackTrace(rl.getAsPrintStream());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String text = "";
        while(!text.equals("exit")) {
            System.out.print("Enter your text: ");

            try {
                text = reader.readLine();
            } catch(IOException e) {
                e.printStackTrace(rl.getAsPrintStream());
            }

            byte[] bytes = (text).getBytes();
            System.out.println("String Byte Length: " + bytes.length);
            rl.log(text);
        }

        rl.close();
    }
}

