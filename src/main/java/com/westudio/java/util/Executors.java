package com.westudio.java.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Executors {

    private static final int POOL_SIZE = 50;

    private static LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();

    private static Lazy<ThreadPoolExecutor> executor = new Lazy<ThreadPoolExecutor>() {
        @Override
        protected ThreadPoolExecutor makeObject() {
            ThreadPoolExecutor executor_ = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
                    1, TimeUnit.MINUTES, queue);
            executor_.allowCoreThreadTimeOut(true);
            return executor_;
        }

        @Override
        protected void destroyObj(ThreadPoolExecutor executor_) {
            executor_.shutdown();
            try {
                while (!executor_.awaitTermination(1, TimeUnit.SECONDS)) {/**/}
            } catch (InterruptedException e) {/**/}
        }
    };

    public static void execute(Runnable runnable) {
        executor.getInstance().execute(runnable);
    }

    public static void shutdown() {
        executor.close();
    }
}
