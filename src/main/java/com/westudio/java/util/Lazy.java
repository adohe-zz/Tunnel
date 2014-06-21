package com.westudio.java.util;

public abstract class Lazy<T> implements AutoCloseable {

    private volatile T instance = null;

    protected abstract T makeObject();

    protected abstract void destroyObj(T obj);

    public T getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = makeObject();
                }
            }
        }

        return instance;
    }

    @Override
    public void close() throws Exception {
        synchronized (this) {
            if (instance != null) {
                T instance_ = instance;
                instance = null;
                destroyObj(instance_);
            }
        }
    }
}
