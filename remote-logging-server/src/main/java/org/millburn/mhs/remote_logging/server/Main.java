package org.millburn.mhs.remote_logging.server;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * The entry point of the server, requires a path to a properties file as argument
 *
 * @author Keming Fei
 */
public class Main implements Closeable {
    private final SuperServer ss;
    private final Properties metadata;

    public Main(String propertiesPath) {
        this.metadata = new Properties();
        try {
            this.metadata.load(new FileInputStream(propertiesPath));
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }

        String ip = this.getRequiredStringProperty("ip");
        int port = this.getRequiredIntProperty("port");
        String logFileDirectory = this.getOptionalStringProperty("log_file_directory", "");
        String desiredKey = this.getOptionalStringProperty("desired_connection_key", "");

        System.out.println("ip: " + ip);
        System.out.println("port: " + port);
        System.out.println("log_file_directory: " + logFileDirectory);
        System.out.println("desired_connection_key: " + desiredKey);
        System.out.println("Initializing the super server...");
        this.ss = new SuperServer(ip, port, logFileDirectory, desiredKey);
    }

    public static void main(String[] args) {
        Main main = new Main(args[0]);
        main.run();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            main.ss.close();
            System.out.println("Exiting...");
        }));
    }

    public String getRequiredStringProperty(String key) {
        String s = this.metadata.getProperty(key);

        if(s == null) {
            throw new IllegalArgumentException("Required property: \"" + key + "\" not found.");
        }

        return s;
    }

    public int getRequiredIntProperty(String key) {
        String s = this.metadata.getProperty(key);
        return Integer.parseInt(s);
    }

    public String getOptionalStringProperty(String key, String defaultValue) {
        return this.metadata.getProperty(key, defaultValue);
    }

    public void run() {
        System.out.println("Listening for connections...");
        this.ss.listen();
    }

    @Override
    public void close() {
        this.ss.close();
    }
}
