package org.millburn.mhs.remote_logging.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ClientTests2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        RemoteLogger rl = new RemoteLogger("localhost", 6969, "logger");
        rl.connect();
        Thread.sleep(1000);
        BufferedReader br = new BufferedReader(new FileReader("Ulysses by James Joyce.txt"));
        String s;

        while((s = br.readLine()) != null) {
            rl.log(s);
        }
    }
}
