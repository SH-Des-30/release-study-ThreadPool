package com.study.threadpoolstudy.service;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sishijie@winployee.com
 * @since 2020-07-29
 */
public class MyThreadFactory implements ThreadFactory {

    //线程名字的前缀
    private final String namePrefix;

    //线程池数量计数器
    private final AtomicInteger poolNumber = new AtomicInteger(1);

    //线程计数器
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup threadGroup;

    public MyThreadFactory(String name) {
        SecurityManager securityManager = System.getSecurityManager();
        threadGroup = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (StringUtils.isBlank(name)){
            name = "ThreadPool";
        }

        namePrefix = name +
                "-" +
                poolNumber.getAndIncrement() +
                "-" +
                "thread" +
                "-";


    }

    @Override
    public Thread newThread(Runnable r) {

        //        myThread.isDaemon();
//        Thread.NORM_PRIORITY;

        return new MyThread(threadGroup, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
    }
}
