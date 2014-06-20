package com.westudio.java.util;

/**
 * Created with IntelliJ IDEA.
 * User: tonyhe
 * Date: 14-6-20
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
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
