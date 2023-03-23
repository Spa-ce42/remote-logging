package org.millburn.mhs.remote_logging.server;

import java.io.File;
import java.time.Instant;

/**
 * Describes objects that create FileAppender, the FileAppender are usually pointed under the same directory, but different files
 *
 * @author Keming Fei
 */
public class FileAppenderFactory {
    private final File logFileDirectory;

    /**
     * @param s a directory that hosts all the log files
     */
    public FileAppenderFactory(String s) {
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

    /**
     * @return the directory that hosts all the log files
     */
    public File getLogFileDirectory() {
        return this.logFileDirectory;
    }

    /**
     * @return the directory but in String format
     */
    public String getLogFileDirectoryString() {
        return this.logFileDirectory.getPath() + File.separatorChar;
    }

    /**
     * @param loggerName the name of the logger
     * @return a FileAppender with a new file opened, almost all files created will not have a name conflict
     * The name of the files contain
     *  - The date, time, in UTC
     *  - The name of the Logger
     */
    public FileAppender createFileAppender(String loggerName) {
        return new FileAppender((this.getLogFileDirectoryString() + (Instant.now() + "-" + loggerName).replace(':', '-') + ".log"));
    }
}
