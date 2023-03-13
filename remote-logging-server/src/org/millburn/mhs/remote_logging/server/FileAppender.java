package org.millburn.mhs.remote_logging.server;

import java.io.*;

public class FileAppender implements Closeable {
    private final File f;
    private final FileWriter fw;

    public FileAppender(String path) {
        this.f = new File(path);

        try {
            this.fw = new FileWriter(this.f, true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void append(CharSequence cs) {
        try {
            this.fw.append(cs);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void appendLine(CharSequence cs) {
        this.append(cs);
        this.append(System.lineSeparator());
    }

    @Override
    public void close() throws IOException {
        this.fw.close();
    }
}
