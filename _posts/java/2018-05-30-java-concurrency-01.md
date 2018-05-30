---
layout: post
title:  "Java并发回炉再造之Oracle文档01"
date:   2018-05-30 21:08:00
author:     "Yan"
header-img: "img/study-hard.jpg" # todo
catalog: true
comments: true
tags:
    - java
---

```
写在最前面:

最近在找国内的工作，投了很多阿里的。面试的时候总在问Java并发编程的问题，备受打击。
在新西兰5年，并没有很多机会接触并发编程。毕业后只是零零碎碎看过一些。由此决定系统的重新学习一下。
```

原文: [https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)

# 什么是并发

当今计算机系统可以在同一时间处理一个或者多个任务。比如视频网站，一个任务在stream data，一个任务去process data。Java在语言层面和library层面都做出了对并发编程的支持。Java的java.util.concurrent是在JDK 5.0以后引入的。

![parellel vs concurrent]({{ site.url }}/img/con_and_par.jpg)

# Processes (进程) vs Thread (线程)

进程和线程是并发编程的两个基础单位。在Java并发编程中，我们更关心Thread。但需要注意，进程同样重要。

一个系统里通常有多个活跃的进程和线程。如果系统中只有一个execution core，那么被执行的线程也只会有一个。这种情况下，通过OS的一个叫做time slicing的功能使single core的处理时间会被不同的进程分享。

## Processes

一个进程有自己的运行环境。进程的运行时资源是独占的，更进一步说，每个进程都有自己的memory空间。

进程间通过OS提供的Inter Process Communication(IPC)来通信。IPC不仅可以支持同一个OS的不同进程通信，也支持不同OS里的两个进程间通信。

大多数Java都只有一个进程。如果需要创建新的进程需要使用[ProcessBuilder](https://docs.oracle.com/javase/9/docs/api/java/lang/class-use/ProcessBuilder.html)类。

## Threads

在Java中，通常可以把线程看作一个轻量级的进程。创建线程所要消耗掉的资源少于进程。与进程类似，每个线程都有自己的运行环境。

每一个进程里至少又一个线程。每个线程都会分享所在进程的资源，比如内存。因此，线程之间的通信是十分有效却又容易产生问题的过程。

![java thread process overview]({{ site.url }}/img/jvm_threads_overview.png)


从一个Java app开发者角度来看，每一个Java app启动的时候都会有一个主线程(main thread)。这个主线程可以用来创建新的线程。

# Thread Object 

在Java中，我们通常使用两种创建和管理线程的方法。一个是通过[Thread](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html)在需要的时候创建线程异步的处理任务。另一种方法则是通过[Executor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html)。

## Runnable & Thread Object

Java提供了两种创建Thread Object并提供Thread需要运行的代码的方法。一种是通过Runnable接口。一种是继承Thread类。以下代码是两种方法的简单实现。

{% highlight java %}

package com.yan.concurrent;

class HelloRunnable implements Runnable {

   @Override
   public void run() {
       System.out.println("Hello from a thread!");
   }

   public static void main(String[] args) {
       new Thread(new HelloRunnable()).start();
       new Thread(() -> System.out.println("Use lambda for runnable")).start();
   }
}

class HelloThread extends Thread {
   @Override
   public void run() {
       System.out.println("Hello Thread");
   }

   public static void main(String[] args) {
       new HelloThread().run();
   }
}

{% endhighlight %}

不论哪一种方法，在运行时都是通过调用Thread.start来创建并执行线程。开发者需要在Thread.run或Runnable.run中提供线程需要的代码。这也是Runnable的存在的意义。在JDK8以后，Runnable继承了Functional接口，因此可以用更简洁的Lambda表达方式来创建一个线程。

第一种方法是被广泛提倡的方法。因为他使用接口，所以允许这个类继承更多不同的类从而更灵活的。

## Thread.sleep

通过调用Thread.sleep来暂停当前线程一段时间。下面是一个简单的实现

{% highlight java %}

package com.yan.concurrent;

public class SleepMessages {
   public static void main(String args[])
           throws InterruptedException {
       String importantInfo[] = {
               "Mares eat oats",
               "Does eat oats",
               "Little lambs eat ivy",
               "A kid will eat ivy too"
       };

       for (String anImportantInfo : importantInfo) {
           //Pause for 4 seconds
           Thread.sleep(4000);
           //Print a message
           System.out.println(anImportantInfo);
       }
   }
}

{% endhighlight %}


### Thread.interrupt & Thread.interrupted

Thread.interrupt是用来干扰当前线程停止目前的任务，做别的任务。比如说，当当前线程正在执行Thread.sleep时，Thread.interuppt会停止线程休眠状态并继续执行下一个任务。一下代码是一个简单的实现

{% highlight java %}

package com.yan.concurrent;

public class InterruptExample {

   public static void main(String[] args) throws InterruptedException {
       Thread a = new Thread(() -> {
           String importantInfo[] = {
                   "Mares eat oats",
                   "Does eat oats",
                   "Little lambs eat ivy",
                   "A kid will eat ivy too"
           };

           for (String anImportantInfo : importantInfo) {
               //Pause for 4 seconds
               try {
                   Thread.sleep(4000);
               } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
                   System.out.println("caught interrupt exception");
               }

               if (Thread.interrupted()) {
                   return;
               }
               //Print a message
               System.out.println(anImportantInfo);
           }
       });

       a.start();

       Thread.sleep(1000);

       a.interrupt();
   }
}

{% endhighlight %}

Thread.interrupt会在当前的线程isInterrupted编程true，当下一条指令开始执行时，会被设为false。因此，只有在`catch (InterruptedException e)`中重新暂停当前线程才会使得Thread.interrupted返回true。

## Thread.join

在多线程的程序中，我们往往需要使优先级高的线程现运行。这就是Thread.join的作用。我们也可以设置join的时间限制。下面是一个简单的例子

{% highlight java %}

package com.yan.concurrent;

import java.util.Arrays;

public class JoinExample {

   public static void main(String[] args) throws InterruptedException {
       Thread a = new Thread(() -> {
           int[] msgs = {1, 2, 3, 4};
           Arrays.stream(msgs).forEach(System.out::println);
       });

       Thread b = new Thread(() -> {
           int[] msgs = {5, 6, 7, 8};
           Arrays.stream(msgs).forEach(System.out::println);
       });

       Thread c = new Thread(() -> {
           int[] msgs = {10, 11, 12, 13};
           Arrays.stream(msgs).forEach(System.out::println);
       });

       a.start();
       a.join();
       b.start();
       b.join();
       c.start();
   }
}

{% endhighlight %}

如果没有使用Thread.join则打印数字顺序是不定的。当我们使用join的时候，数字的顺序是一定的。