package com.join;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * @author chi  2018-06-22 11:39
 **/
public class CountTask extends RecursiveTask<Integer> {

    private int start;
    private int end;

    private static final int THRED_HOLD = 30;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {

        int sum = 0;
        boolean canCompute = (end - start) <= THRED_HOLD;
        if (canCompute) {
            //如果任务足够小就计算任务
            for (int i = start; i <= end; i++) {
                sum += i;
            }
            System.out.println("thread: " + Thread.currentThread() + " start: " + start + " end: " + end);
        } else {
            //如果任务大于阀值，就分裂成两个子任务计算
            int mid = (end + start) / 2;
            CountTask left = new CountTask(start, mid);
            CountTask right = new CountTask(mid + 1, end);
            //执行子任务
            left.fork();
            right.fork();
            //等待子任务执行完，并得到其结果
            //合并子任务
            sum = left.join() + right.join();
        }

        return sum;
    }


    public static void main(String[] args) throws Exception{
        int start = 0;
        int end = 100;

        CountTask task = new CountTask(start, end);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Future<Integer> ans = pool.submit(task);
        int sum = ans.get();
        System.out.println(sum);
    }
}
