---
layout: post
title:  "Java并发回炉再造之Oracle文档二"
date:   2018-05-31 22:49:00
author:     "Yan"
header-img: "img/sync-title.jpg"
catalog: true
comments: true
tags:
    - java
---

原文: [https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)

# Synchronization

Synchronization的用处是什么？由于Thread之间的通信是通过共享field access和object reference fields实现的。
所以Thread间的通信十分高效。但同时带来了两个问题一个是Thread interference，另一个是memeory consistency。
这两个问题都是由于多个线程试图使用或改变同一个资源而产生的。如果我们使用Syncroniation，我们可以强制Thread按顺序先后
访问这个资源，从而避免Thread的通信带来的以上两个问题。

但是有些时候，Synchronization并不是一个高效的解决问题的方式。比如说，当一个Thread长时间，或者频繁占用一个资源的时候，
其他Thread就不得不排队等候其完成任务后才能继续执行(Thread Starvation)。

## Thread Interference

下面是一个简单的Thread interference的例子。我们跑一个测试100次。可以发现，结果并不总是0。

{% highlight java %}

package com.yan.concurrent;

public class Counter {

   private int c = 0;

   public void increment() {
       c++;
   }

   public void decrement() {
       c--;
   }

   public int value() {
       return c;
   }

   public static void main(String[] args) {

       for (int j = 0; j < 100; j++) {
           Counter counter = new Counter();

           Thread a = new Thread(() -> {
               for (int i = 0; i < 100; i++) {
                   counter.increment();
               }
           });

           Thread b = new Thread(() -> {
               for (int i = 0; i < 100; i++) {
                   counter.decrement();
               }
           });

           a.start();
           b.start();
           // 等待a和b都完成
           while(a.isAlive() || b.isAlive()) {}
           // 结果并不都为0
           System.out.println(counter.value());
       }
   }
}
{% endhighlight %}

## Memory Consistency Errors

这个问题描述的就是几个Thread对于同一个数据得出了不同的值。比如Thread-a对`int c = 0`做了`c+=1`。
当同时运行的Thread-b使用c时，并不能保证`c=1`。

为了确保不会发生此类问题。只需要保证happen-before relationship。比如说用Synchroniza确保两个Thread先后访问c。
更多的解决方案可以参考[这个](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility).

## Synchronized Methods

Java中有Synchronized methods和Synchronized statements。

{% highlight java %}
package com.yan.concurrent;

public class SynchronizedCounter {
   private int c = 0;

   public synchronized void increment() {
       c++;
   }

   public synchronized void decrement() {
       c--;
   }

   public synchronized int value() {
       return c;
   }

   public static void main(String[] args) {

       SynchronizedCounter counter = new SynchronizedCounter();
       for (int j = 0; j < 100; j++) {
           Thread a = new Thread(() -> {
               for (int i = 0; i < 100; i++) {
                   counter.increment();
               }
           });

           Thread b = new Thread(() -> {
               for (int i = 0; i < 100; i++) {
                   counter.decrement();
               }
           });
           a.start();
           b.start();
           // 等待a和b都完成
           while(a.isAlive() || b.isAlive()) {}
           // 结果都为0
           System.out.println(counter.value());
       }
   }
}
{% endhighlight %}

当一个线程完成对一个synchronized method调用后第二个线程开始调用同一个method，Happen-before relationship会自动建立。

```
需要注意：

syncronized不能用在constructor上。因为只有建立对象的线程有权调用constructor。
当对象建立的过程中，其他线程可以通过对向上的field对正在建立的对象进行访问。
```

## Intrinsic Locks and Synchronization 

Intrinsic Locks是Synchronization的实现基础之一。Intrinsic Locks确保了Thread对对象state的独立访问并且为不同的Thread
对对象访问时简历happen-before relationship

当一个Thread需要访问对象时必须获得intrinsic lock，否则要等待锁再一次可用。当Thread拥有锁时就可以访问对象，当完成访问时必须释放锁以便其他Thread可以继续访问对象。intrinsic lock只有一个，不会被多个Thread分享。

{% highlight java %}
public void addName(String name) {
   synchronized(this) {
       lastName = name;
       nameCount++;
   }
   nameList.add(name);
}
{% endhighlight %}

以上是以对象为锁。当然，也可在同一个对象里创建不同的对象为锁，以便更灵活的访问对象的method。如下：

{% highlight java %}

public class MsLunch {
   private long c1 = 0;
   private long c2 = 0;
   private Object lock1 = new Object();
   private Object lock2 = new Object();

   public void inc1() {
       synchronized(lock1) {
           c1++;
       }
   }

   public void inc2() {
       synchronized(lock2) {
           c2++;
       }
   }
}

{% endhighlight %}

当一个Thread直接或间接的调用了多个synchronized的代码，而这些代码的锁都是同一个对象/锁。这样的模式叫Reentrant Synchronization。

## Atomic Access

Atomic代表了要么发生要么不发生。只有当其完成执行之后才会得到程序运行的影响(或者边际效益)。 因此当时用atomic的时候不用担心Thread Interference。

- 在Java中，所有的reference variables和primitive variables(除了long和都变了)的读和写都是atomic的
- 被volatile修饰的变量的读和写也都是atomic的，包括long和doble

```
需要注意的是：

memroy consistency errors(mce)仍然有可能发生。
但是使用volatile可以极大的降低mce发生的可能性。
```

使用atomic variables比synchronized要更加有效。但需要开发者注意mce的发生。
