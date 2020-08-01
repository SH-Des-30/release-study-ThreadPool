package com.study.threadpoolstudy.web;

import com.study.threadpoolstudy.service.MyThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.*;

/**
 * @author sishijie@winployee.com
 * @since 2020-07-27
 */
public class ThreadPoolDemo {


    /**
     * 提交15个任务，让线程池执行，每个任务执行3秒钟，通过控制台的日志，查看线程池的中的线程状态。
     * @param threadPoolExecutor   不同类型的线程池
     */
    private void submitThreadTask(ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {

        if (threadPoolExecutor == null) {
            return;//线程池不存在
        }

        //1、提交15个任务
        for (int i = 1; i < 16; i++) {
            int count = i;
            threadPoolExecutor.submit(() -> {
                //通过睡眠的方式模拟3秒的处理业务时间
                String threadName = Thread.currentThread().getName();
                try {
                    System.out.println(threadName + "开始执行：" + count);
                    Thread.sleep(3000L);
                    System.out.println(threadName + "结束执行：" + count);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            System.out.println("提交成功数量：" + count);
        }


        //2、休眠0.5秒等待任务全部条，查看线程池的状态
        Thread.sleep(500L);
        System.out.println("当前线程池的线程数量：" + threadPoolExecutor.getPoolSize());
        System.out.println("当前线程池的等待数量：" + threadPoolExecutor.getQueue().size());

        threadPoolExecutor.shutdown();
        System.out.println("关闭线程池成功：" + threadPoolExecutor.getQueue().size());

        //3、休眠15秒等待线程池任务全部执行完毕， 此时超出核心线程的线程应该会被销毁
        Thread.sleep(15000L);

        System.out.println("当前线程池的线程数量：" + threadPoolExecutor.getPoolSize());
        System.out.println("当前线程池的等待数量：" + threadPoolExecutor.getQueue().size());

    }

    /**
     * 线程池配置：核心线程：5，最大线程数量为10，活跃时间为3秒，无界队列，不指定拒绝策略
     * 执行任务：使用线程池提交15个任务，每个任务需要3秒
     * 期望结果：线程在使用无界队列，只会开启5分线程去执行任务，其余会进入排队
     * 结论：判断是否超过核心线程数量
     *         否：开启新的新城执行任务；
     *         是：判断队列是否已满
     *                否：进入队列等待执行
     *                是：判断是否达到最大线程数量
     *                      否：开启一个新的线程执行任务
     *                      是：执行拒绝策略
     *
     * 依据： 根绝执行的结果，加上java.util.concurrent.ThreadPoolExecutor#execute(java.lang.Runnable)源码
     *
     * 应用场景：只有一个一步任务执行的时候可以使用当前模式.
     * 注意：需要注意如果是无界队列，任务过多，大量任务在排队中，导致系统出现大规模停顿的问题，出现OOM的问题，不建议直接使用无界队列线程池
     */
    private void testThreadPoolTest1() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new MyThreadFactory("testThreadPoolTest1"));

        submitThreadTask(threadPoolExecutor);

    }


    /**
     * 线程池配置：
     *      核心线程数5，最大线程10，超出核心线程空闲时的存活时间3秒，最大队列3,自定义线程工厂，更好的管理线程
     * 应用场景：
     *      当线程数量开到一定限度的时候，我们根据拒绝策略，使用更加丰富的降级策略来处理业务逻辑，防止系统因线程队列
     *      太多阻塞太多的线程，而出现内存溢出的问题。
     *
     */
    private void testThreadPoolTest2() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                new MyThreadFactory("testThreadPoolTest2"));

        submitThreadTask(threadPoolExecutor);

    }

    /**
     * 线程池配置：
     *      核心线程0，最大线程数Integer.MAX_VALUE，空闲存活时间3秒，有界队列3
     * 应用场景：
     *      核心线程为0，有任务需要执行的时候，会去开一个线程去执行任务，但是当有空闲的线程
     *      会直接从池里面获取，然后执行线程，在世纪的开发场景中，可以适当的自定义一下最大线程数量，
     *      当我们无法确定具体的任务数量，并且线程任务不是密集计算型就可以采用当前模式
     */
    private void testThreadPoolTest3() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                new MyThreadFactory("testThreadPoolTest3"));

        submitThreadTask(threadPoolExecutor);

    }


    private void testThreadPoolTest4() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                new MyThreadFactory("testThreadPoolTest3"));
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        submitThreadTask(threadPoolExecutor);

    }

    



    public static void main(String[] args) throws InterruptedException {
        ThreadPoolDemo threadPoolDemo = new ThreadPoolDemo();

//        threadPoolDemo.testThreadPoolTest1();
//        threadPoolDemo.testThreadPoolTest2();
        threadPoolDemo.testThreadPoolTest3();

    }
}
