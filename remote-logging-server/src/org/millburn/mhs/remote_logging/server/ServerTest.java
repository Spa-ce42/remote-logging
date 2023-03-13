package org.millburn.mhs.remote_logging.server;

import java.io.IOException;
import java.time.Instant;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        FileAppender fa = new FileAppender((Instant.now() + "-Kiosk-1.log").replace(':', '-'));
        fa.appendLine("Hello world!");
        fa.close();
    }
}
