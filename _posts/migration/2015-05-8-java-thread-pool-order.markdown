---
layout: post
title:  "Random Execution Order by using Java ThreadPool "
date:   2015-05-08 10:00:00
categories: java
---

This post is to remind myself that when using Java thread Pool created by ```java.util.concurrent.ExecutorService``` to execute amount of threads, there is going to be no execution orders.

The following program is a proof:

{% highlight java %}

package com.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by daiyan on 8/05/15.
 */
public class SampleThreadPool
{

    private ExecutorService threadPools;

    SampleThreadPool() throws InterruptedException
    {
        threadPools = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 100; i++)
        {
            threadPools.submit(new MyThread(i + ""));
        }

        threadPools.shutdown();

        threadPools.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }


    class MyThread implements Runnable {

        String message;

        MyThread(String message) {
            this.message = message;
        }

        @Override
        public void run()
        {
            System.out.println(Thread.currentThread().getName() + ": " + message );
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        SampleThreadPool sampleThreadPool = new SampleThreadPool();

        while (!sampleThreadPool.threadPools.isTerminated())
        {

        }
    }
}


{% endhighlight %}

The result will look like this:


{% highlight java %}

pool-1-thread-1: 0
pool-1-thread-4: 3
pool-1-thread-3: 2
pool-1-thread-2: 1
pool-1-thread-5: 4
pool-1-thread-6: 5
pool-1-thread-7: 6
pool-1-thread-8: 7
pool-1-thread-9: 8
pool-1-thread-10: 9
pool-1-thread-11: 10
pool-1-thread-12: 11
pool-1-thread-13: 12
pool-1-thread-14: 13
pool-1-thread-15: 14
pool-1-thread-16: 15
pool-1-thread-17: 16
pool-1-thread-18: 17
pool-1-thread-19: 18
pool-1-thread-20: 19
pool-1-thread-21: 20
pool-1-thread-22: 21
pool-1-thread-23: 22
pool-1-thread-24: 23
pool-1-thread-25: 24
pool-1-thread-26: 25
pool-1-thread-27: 26
pool-1-thread-28: 27
pool-1-thread-29: 28
pool-1-thread-30: 29
pool-1-thread-31: 30
pool-1-thread-32: 31
pool-1-thread-33: 32
pool-1-thread-34: 33
pool-1-thread-35: 34
pool-1-thread-36: 35
pool-1-thread-37: 36
pool-1-thread-38: 37
pool-1-thread-39: 38
pool-1-thread-40: 39
pool-1-thread-41: 40
pool-1-thread-42: 41
pool-1-thread-43: 42
pool-1-thread-44: 43
pool-1-thread-45: 44
pool-1-thread-46: 45
pool-1-thread-47: 46
pool-1-thread-48: 47
pool-1-thread-49: 48
pool-1-thread-50: 49
pool-1-thread-1: 50
pool-1-thread-4: 51
pool-1-thread-2: 54
pool-1-thread-50: 53
pool-1-thread-1: 52
pool-1-thread-8: 62
pool-1-thread-50: 61
pool-1-thread-3: 60
pool-1-thread-7: 59
pool-1-thread-2: 58
pool-1-thread-6: 57
pool-1-thread-5: 55
pool-1-thread-4: 56
pool-1-thread-5: 70
pool-1-thread-6: 69
pool-1-thread-2: 68
pool-1-thread-6: 73
pool-1-thread-7: 67
pool-1-thread-3: 66
pool-1-thread-50: 65
pool-1-thread-8: 64
pool-1-thread-1: 63
pool-1-thread-8: 79
pool-1-thread-50: 78
pool-1-thread-3: 77
pool-1-thread-7: 76
pool-1-thread-6: 75
pool-1-thread-2: 74
pool-1-thread-5: 72
pool-1-thread-4: 71
pool-1-thread-5: 87
pool-1-thread-2: 86
pool-1-thread-6: 85
pool-1-thread-7: 84
pool-1-thread-3: 83
pool-1-thread-50: 82
pool-1-thread-8: 81
pool-1-thread-8: 95
pool-1-thread-1: 80
pool-1-thread-8: 96
pool-1-thread-50: 94
pool-1-thread-3: 93
pool-1-thread-7: 92
pool-1-thread-6: 91
pool-1-thread-2: 90
pool-1-thread-5: 89
pool-1-thread-4: 88
pool-1-thread-50: 99
pool-1-thread-8: 98
pool-1-thread-1: 97

{% endhighlight %}
