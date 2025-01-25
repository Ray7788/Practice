package ConcurrentStudy;

import java.util.concurrent.CountDownLatch;

/**
 * 使用 Implements Runnable 实现三个线程循环顺序打印 1-100，结合 CountDownLatch
 */
public class CountDownLatchStudy2 {

    private static int number = 1; // 共享变量，用于记录当前打印的数字
    private static final int total = 100; // 总数字范围

    public static void main(String[] args) {
        printNumbers();
    }

    static void printNumbers() {
        CountDownLatch latch = new CountDownLatch(3); // 用于线程结束的计数器

        // 创建三个线程，分别打印序列中的数字
        Thread t1 = new Thread(new PrintTask(1, latch), "Thread-1");
        Thread t2 = new Thread(new PrintTask(2, latch), "Thread-2");
        Thread t3 = new Thread(new PrintTask(0, latch), "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class PrintTask implements Runnable{
        private final int threadId;
        private final CountDownLatch latch;

        public PrintTask(int threadId, CountDownLatch latch){
            this.threadId = threadId;
            this.latch = latch;
        }

        @Override
        public void run(){
            while (number <= total) {
                synchronized (CountDownLatchStudy2.class) {
                    // 判断是否轮到当前线程打印 加上判断 number <= total 防止多线程情况下超过100
                    if (number % 3 == threadId && number <= total) {
                        System.out.println(Thread.currentThread().getName() + " " + number);
                        number++;
                    }
                }
            }
            latch.countDown();
        } 

    }
}