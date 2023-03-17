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

    public Main(String propertiesPath) throws IOException {
        this.metadata = new Properties();
        this.metadata.load(new FileInputStream(propertiesPath));
        this.ss = new SuperServer(
                this.getRequiredStringProperty("ip"),
                this.getRequiredIntProperty("port"),
                this.getOptionalStringProperty("logFileDirectory", ""),
                this.getOptionalStringProperty("desiredKey", "1234")
        );
    }

    public static void main(String[] args) {
        try(Main main = new Main(args[0])) {
            main.run();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
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
        this.ss.listen();
    }

    @Override
    public void close() {
        this.ss.close();
    }
}
