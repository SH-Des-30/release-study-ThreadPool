package com.study.threadpoolstudy.service;

import java.util.Date;

/**
 * @author sishijie@winployee.com
 * @since 2020-07-29
 */
public class MyThread extends Thread{

    //线程创建的时间
    private Date createDate;

    //开始的执行时间
    private Date startDate;

    //结束的执行时间
    private Date endDate;

    public MyThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        this.createDate = new Date();
    }

    @Override
    public void run() {
        setStartDate();
        super.run();
        setEndDate();
    }

    private synchronized void setStartDate(){
        startDate = new Date();
    }

    private synchronized void setEndDate(){
        endDate = new Date();
    }

    private synchronized long getExecutionTime(){
        if (endDate == null)
            endDate = new Date();
        return  endDate.getTime() - startDate.getTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getName())
                .append("，")
                .append("Create Date：")
                .append(createDate)
                .append("，")
                .append("Running Time：")
                .append(getExecutionTime())
                .append(" Milliseconds.");

        return sb.toString();
    }
}
