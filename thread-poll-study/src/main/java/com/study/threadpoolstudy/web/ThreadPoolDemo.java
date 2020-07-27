package com.study.threadpoolstudy.web;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
                try {
                    System.out.println("开始执行：" + count);
                    Thread.sleep(3000L);
                    System.out.println("结束执行：" + count);

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
     * 依据： 根绝执行的结果，加上ThreadPoolExecutor.execute()源码
     *
     */
    private void testThreadPoolTest1() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());

        submitThreadTask(threadPoolExecutor);

    }



    public static void main(String[] args) throws InterruptedException {
        ThreadPoolDemo threadPoolDemo = new ThreadPoolDemo();

        threadPoolDemo.testThreadPoolTest1();

    }
}
