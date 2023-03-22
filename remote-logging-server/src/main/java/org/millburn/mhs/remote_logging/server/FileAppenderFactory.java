package org.millburn.mhs.remote_logging.server;

import java.io.File;
import java.io.UncheckedIOException;
import java.time.Instant;

public class FileAppenderFactory {
    private File logFileDirectory;

    public FileAppenderFactory() {

    }

    public File getLogFileDirectory() {
        return this.logFileDirectory;
    }

    public void setLogFileDirectory(String s) {
        File f = new File(s);

        if(f.isFile() && f.exists()) {
            throw new IllegalArgumentException(f + " exists and is a file.");
        }

        if(!f.exists()) {
            boolean b = f.mkdirs();

            if(!b) {
                throw new RuntimeException("Cannot create: " + f);
            }
        }

        this.logFileDirectory = f;
    }

    public String getLogFileDirectoryString() {
        return this.logFileDirectory.getPath() + File.separatorChar;
    }

    public FileAppender createFileAppender(String loggerName) {
        return new FileAppender((this.getLogFileDirectoryString() + (Instant.now() + "-" + loggerName).replace(':', '-') + ".log"));
    }
}
