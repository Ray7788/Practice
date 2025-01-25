package ConcurrentStudy;

import java.util.concurrent.Semaphore;

/**
 * 使用 Semaphore 实现线程按序打印 1-100
 */
public class SemaphoreSolution2 {
    private static int n = 1;   // 共享数据
    private static final int total = 100; // 总数字范围

    public static void main(String[] args) {
        printNumbers();
    }
    
    static void printNumbers(){
        Semaphore s1 = new Semaphore(1);    // 初始信号量为1: 保证第一个线程先执行
        Semaphore s2 = new Semaphore(0);    // 初始信号量为0: 保证第二个线程后执行
        Semaphore s3 = new Semaphore(0);    // 初始信号量为0: 保证第三个线程最后执行

        Thread t1 = new Thread(new PrintTask(1, s1, s2), "Thread-1");
        Thread t2 = new Thread(new PrintTask(2, s2, s3), "Thread-2");
        Thread t3 = new Thread(new PrintTask(0, s3, s1), "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }

    static class PrintTask implements Runnable{
        private final int threadId;
        private final Semaphore current;
        private final Semaphore next;

        public PrintTask(int threadId, Semaphore current, Semaphore next){
            this.threadId = threadId;
            this.current = current;
            this.next = next;
        }

        @Override
        public void run(){
            while (n <= total) {
                try {
                    current.acquire();  // 获取当前线程的信号量
                    if (n <= total && n % 3 == threadId) {
                        System.out.println(Thread.currentThread().getName() + " " + n);
                        n++;
                    }
                    next.release(); // 释放下一个线程的信号量
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
