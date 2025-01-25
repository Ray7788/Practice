package ConcurrentStudy;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class NamingThreadFactory implements ThreadFactory {

    public final ThreadFactory threadFactory;
    //线程名称
    public final String name;
    //原子类，用于保证多个线程创建递增的名称id
    private final AtomicInteger ThreadNum = new AtomicInteger(1);

    public NamingThreadFactory(ThreadFactory threadFactory, String name) {
        this.threadFactory = threadFactory;
        this.name = name;
    }

    //重写newThread方法，自定义线程名称
    @Override
    public Thread newThread(Runnable r) {
        Thread t = threadFactory.newThread(r);
        t.setName(name + ThreadNum.getAndIncrement());
        return t;
    }

}