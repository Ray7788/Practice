package ConcurrentStudy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    public static int total = 100;    // 最大目标数
    public static volatile int n = 1;   // volatile保证变量可见性
    public static Object lock = new Object();   // 同步锁，保证原子性

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3,
                4, // 
                0L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                //自定义线程工厂，自定义线程名称
                new NamingThreadFactory(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r);
                    }
                }, "线程"),
                //拒绝策略为回退给原线程
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 1; i <= 3; i++) {
            threadPoolExecutor.execute(new MyTask(i));
        }

        // 关闭线程池,等待线程池中的线程执行完毕
        try {
            threadPoolExecutor.shutdown();
            // awaitTermination可以等待所有线程完成，或者超时时间到达
            if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                // 如果超时，选择强制关闭线程池
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //继承Runnable类实现Run方法
    public static class MyTask implements Runnable {

        private int threadId;

        MyTask(int threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {
            // 同步锁保证原子性,放在外面能最后释放锁，程序退出，放在里面由于先释放后等待锁，因此无法释放锁程序不会退出
            synchronized (lock) {
                while (n <= total) {
                    while ((n + 2) % 3 + 1 == threadId) {
                        System.out.println("Thread " + Thread.currentThread().getName() + ": " + (n++));
                        lock.notifyAll();
                    }

                    try {
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                lock.notifyAll();      // 释放空闲的锁
            }
        }
    }
}
