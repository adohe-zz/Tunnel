package com.westudio.java.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Executors {

    private static final int POOL_SIZE = 50;

    private static AtomicInteger threadNum = new AtomicInteger(0);

    private static LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();

    public static int getThreadNum() {
        return threadNum.get();
    }

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

    public static void execute(final Runnable runnable) {
        executor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                threadNum.incrementAndGet();
                try {
                    runnable.run();
                } catch (RuntimeException e) {

                } finally {
                    threadNum.decrementAndGet();
                }
            }
        });
    }

    public static void shutdown() {
        executor.close();
    }
}
