package me.imrashb.discord.routines;

import java.time.*;
import java.util.concurrent.*;

public abstract class PeriodicRoutine implements Runnable {

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private Duration period;
    private boolean started = false;
    private ScheduledFuture routine = null;

    PeriodicRoutine(Duration period) {
        this.period = period;
    }

    public boolean startRoutine(final Duration initialDelay) {
        if(!started) {
            routine = EXECUTOR.scheduleAtFixedRate(this, initialDelay.getSeconds(), period.getSeconds(), TimeUnit.SECONDS);
            started = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean startRoutine() {
        return startRoutine(Duration.ofSeconds(0));
    }

    public boolean stopRoutine() {
        if(started) {
            routine.cancel(false);
            routine = null;
            started = false;
            return true;
        } else {
            return false;
        }
    }

}
