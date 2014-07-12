package com.westudio.java.util;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Conf {

    public static boolean handleShutdown(Class<?> clazz, String[] args,
        final AtomicBoolean running) {
        if (args != null && args.length > 0 && args[0].equals("stop")) {
            running.set(false);
            return true;
        }

        SignalHandler handler = new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                running.set(false);
            }
        };

        String command = System.getProperty("sun.java.command");
        if (command == null || command.isEmpty()) {
            return false;
        }

        if (clazz.getName().equals(command.split(" ")[0])) {
            Signal.handle(new Signal("INT"), handler);
            Signal.handle(new Signal("TERM"), handler);
        }

        return false;
    }

    public static Logger openLogger(String name, int limit, int count) {
        Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);

        FileHandler handler = null;

        handler.setLevel(Level.INFO);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);

        return logger;
    }

    public static void closeLogger(Logger logger) {

    }
}
