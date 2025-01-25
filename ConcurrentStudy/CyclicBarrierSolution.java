package ConcurrentStudy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 使用 CyclicBarrier 实现线程按序打印 1-100
 */
public class CyclicBarrierSolution {

    // 共享变量，表示当前要打印的数字
    private static int number = 1;

    // 参与线程数量
    private static final int threadNum = 3;

    // 循环屏障，指定三个线程为屏障点
    private static final CyclicBarrier barrier = new CyclicBarrier(threadNum, new Runnable() {
        @Override
        public void run() {
            // 屏障点动作，当前不需要额外的操作
            // 该Runnable在所有线程到达屏障后执行
        }
    });

    public static void main(String[] args) {
        // 创建三个线程，分别打印序列中的数字
        Thread threadA = new Thread(new NumberPrinter(0), "Thread-1");
        Thread threadB = new Thread(new NumberPrinter(1), "Thread-2");
        Thread threadC = new Thread(new NumberPrinter(2), "Thread-3");

        // 启动三个线程
        threadA.start();
        threadB.start();
        threadC.start();
    }


    
    // 内部静态类实现 Runnable，传入线程的序号 mod 值
    static class NumberPrinter implements Runnable {
        private final int threadId;

        public NumberPrinter(int threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (CyclicBarrierSolution.class) {
                        // 判断当前线程是否可以打印
                        // if (number > 100) {
                        //     break; // 如果超过100，结束循环
                        // }
                        if (number % threadNum == threadId && number <= 100) {
                            System.out.println(Thread.currentThread().getName() + ": " + number);
                            number++; // 打印后递增数字
                        }
                    }
                    // 等待其他线程到达屏障点
                    barrier.await();
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
