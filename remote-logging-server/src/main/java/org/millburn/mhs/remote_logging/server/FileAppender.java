package org.millburn.mhs.remote_logging.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FileAppender {
    private final File dir;
    private ScheduledExecutorService ses;
    private final AtomicReference<FileWriter> fw;


    private void endCurrentStartNext() {
        try {
            if(this.fw.get() != null) {
                this.fw.get().close();
            }
        } catch(IOException ignored) {
            //Ignored, we will construct a new one anyway.
        }

        ZonedDateTime zdt = ZonedDateTime.now();
        String fileName = zdt.getYear() + "-" + zdt.getMonthValue() + "-" + zdt.getDayOfMonth() + "-" + zdt.getHour() + "-" + zdt.getMinute() + "-" + zdt.getSecond() + "-" + zdt.getNano() / 1000000 + ".log";

        try {
            FileWriter fx = new FileWriter(new File(this.dir, fileName), true);
            this.fw.set(fx);
        } catch(IOException e) {
            //Sucks to suck, maybe put out some errors somewhere.
            e.printStackTrace();
        }
    }

    public FileAppender(String dir, ZonedDateTime initialDateTime, long delayMillis) {
        this.fw = new AtomicReference<>();
        this.dir = new File(dir);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRun = initialDateTime;

        if(now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        Duration d = Duration.between(now, nextRun);
        long initialDelay = d.get(ChronoUnit.NANOS);

        this.ses = Executors.newSingleThreadScheduledExecutor();
        System.err.println(initialDelay);
        System.err.println(initialDateTime);
        this.ses.scheduleAtFixedRate(this::endCurrentStartNext, TimeUnit.NANOSECONDS.toMillis(initialDelay), delayMillis, TimeUnit.MILLISECONDS);
    }

    public void append(String s) {

        try {
            System.err.println("Appending: " + s);
            this.fw.get().append(s);
        } catch(IOException e) {
            //Probably put more errors somewhere
        }
    }

    public void appendLine(String s) {
        this.append(s);
        this.append(System.lineSeparator());
    }
}
