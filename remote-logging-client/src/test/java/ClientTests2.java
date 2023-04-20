import org.millburn.mhs.remote_logging.client.RemoteLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ClientTests2 {
    public static void main(String[] args) throws IOException {
        RemoteLogger rl = new RemoteLogger("localhost", 6969, "logger");
        rl.connect();
        BufferedReader br = new BufferedReader(new FileReader("Ulysses by James Joyce.txt"));
        new Scanner(System.in).nextLine();
        StringBuilder sb = new StringBuilder();
        String s;
        while((s = br.readLine()) != null) {
            sb.append(s);
        }

        rl.log(sb);
        new Scanner(System.in).nextLine();
        rl.close();
    }
}
