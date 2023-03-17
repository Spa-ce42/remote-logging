package org.millburn.mhs.remote_logging.server;

/**
 * The entry point of the server, requires an ip and a port as arguments
 *
 * @author Keming Fei
 */
public class Main {
    public static void main(String[] args) {
        try(SuperServer ss = new SuperServer(args[0], Integer.parseInt(args[1]))) {
            System.out.println("Successfully initialized server, listening for connections...");
            ss.listen();
        }
    }
}
