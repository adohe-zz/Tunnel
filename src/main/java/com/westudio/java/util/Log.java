package com.westudio.java.util;


import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static AtomicReference<Logger> logger_ =
            new AtomicReference<>();

    static {
        Logger.getGlobal().setLevel(Level.INFO);
    }

    public static Logger getAndSet(Logger logger) {
        return logger_.getAndSet(logger);
    }

    private static void log(Level l, Throwable t, String s) {

    }

    public static void w(String msg) {
        log(Level.WARNING, null, msg);
    }

    public static void w(Throwable t) {
        log(Level.WARNING, t, null);
    }

    public static void w(String msg, Throwable t) {
        log(Level.WARNING, t, msg);
    }
}
