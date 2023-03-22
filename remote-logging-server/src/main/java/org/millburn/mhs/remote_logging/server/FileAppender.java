package org.millburn.mhs.remote_logging.server;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Describes an open log file waiting for strings to be appended
 *
 * @author Keming Fei
 */
public class FileAppender implements Closeable {
    private final FileWriter fw;

    /**
     * @param path the path to a file, the file can either exist or not exist
     */
    public FileAppender(String path) {
        File f = new File(path);

        try {
            this.fw = new FileWriter(f, true);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Append the string
     *
     * @param s the string
     */
    public void append(String s) {
        try {
            this.fw.append(s);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Append the string with a new line
     *
     * @param s the string
     */
    public void appendLine(String s) {
        this.append(s);
        this.append(System.lineSeparator());
    }

    public void flush() throws IOException {
        this.fw.flush();
    }

    /**
     * Closes the file
     *
     * @throws IOException if an IO error occur
     */
    @Override
    public void close() throws IOException {
        this.fw.close();
    }
}
