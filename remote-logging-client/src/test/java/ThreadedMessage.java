import org.millburn.mhs.remote_logging.client.RemoteLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadedMessage {
    public static void main(String[] args) throws IOException {
        RemoteLogger rl = new RemoteLogger("localhost", 6969, "logger");
        rl.attemptToConnect();
        new Scanner(System.in).nextLine();
        Random r = new Random();
        BufferedReader br = new BufferedReader(new FileReader("Ulysses by James Joyce.txt"));
        StringBuilder sb = new StringBuilder();
        String s;

        while((s = br.readLine()) != null) {
            sb.append(s);
        }

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);
        ses.scheduleWithFixedDelay(() -> {
            ses.scheduleWithFixedDelay(() -> {
                rl.log(sb);
            }, r.nextInt(5), r.nextInt(3), TimeUnit.SECONDS);
        }, 0, 1, TimeUnit.SECONDS);
    }
}
