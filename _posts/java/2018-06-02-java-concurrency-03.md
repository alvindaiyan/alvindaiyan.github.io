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

# Liveness

在并发编程中，并没有一个十分严谨的对活跃性(liveness)的定义。如果说，安全性就是关心糟糕的事情永远不会发生，那么活跃性就是描述正确的事情会在正确的时间最终会发生。关于并发的活跃性，有几个很经典的问题，比如死锁，饥饿和活锁。

## Deadlock

```
Deadlock describes a situation where two or more threads are blocked forever, waiting for each other
```

一下是一个简单死锁事例。当alphonse和gaston需要执行bowback时，他们都在等待对方线程解锁资源。由此产生死锁。

{% highlight java %}
package com.yan.concurrent;

public class DeadLock {
   static class Friend {
       private final String name;
       public Friend(String name) {
           this.name = name;
       }
       public String getName() {
           return this.name;
       }
       public synchronized void bow(Friend bower) {
           System.out.format("%s: %s"
                           + "  has bowed to me!%n",
                   this.name, bower.getName());
           bower.bowBack(this);
       }
       public synchronized void bowBack(Friend bower) {
           System.out.format("%s: %s"
                           + " has bowed back to me!%n",
                   this.name, bower.getName());
       }
   }

   public static void main(String[] args) {
       final Friend alphonse =
               new Friend("Alphonse");
       final Friend gaston =
               new Friend("Gaston");
       new Thread(() -> alphonse.bow(gaston)).start();
       new Thread(() -> gaston.bow(alphonse)).start();
   }
}
{% endhighlight %}

## Starvation and LiveLock

饥饿问题的产生是由于线程所需要的资源不断的被其他一个或多个线程占用上锁，进而产生由于线程无法得到所需的资源无法执行命令。
比如说，线程T1占用了资源R，线程T2又请求封锁R，于是T2等待。T3也请求资源R，当T1释放了R上的封锁后，系统首先批准了T3的请求，T2仍然等待。然后T4又请求封锁R，当T3释放了R上的封锁之后，系统又批准了T4的请求而不断循环，T2可能永远等待。

活锁和思索本质上一样的。都是由于资源被另一个线程占用而产生的问题。死锁，顾名思义，就是两个线程需要的资源被对方锁住而无法继续执行任务。而活锁就是当线程无法给需要的资源上锁时，会重新开启新线程，关闭老线程重试。由于两个冲突的线程在同一时间重启且资源占用问题没有被结局，所以回不断的重启，尝试加锁。由此产生死循环。

![liveness]({{ site.url }}/img/liveness.jpeg)


# Guarded Blocks

Guarded Blocks是用简单的轮询(polling)一个condition来查验block是否可以继续执行。这样是为了协调不同线程间对资源占用以确保程序可以最有效率的执行。

最简单的例子就是用while loop来检查block的继续执行的条件是否成立。但是这种方法对系统资源是一种极大的浪费。

{% highlight java %}
public void guardedJoy() {
   // Simple loop guard. Wastes
   // processor time. Don't do this!
   while(!joy) {}
   System.out.println("Joy has been achieved!");
}
{% endhighlight %}

另一种方法是调用`Object.wait`来禁止当前的线程。wait方法必须等待相应事件的出发才能返回从而使程序继续执行。

{% highlight java %}
public synchronized void guardedJoy() {
    // This guard only loops once for each special event, which may not
    // be the event we're waiting for.
    while(!joy) {
        try {
            wait();
        } catch (InterruptedException e) {}
    }
    System.out.println("Joy and efficiency have been achieved!");
}
{% endhighlight %}

```
wait方法应该在一个loop里出发。
```

wait方法也可以被interrupted，并且抛出一个InterruptedEception。

当wait方法被调用，线程会暂停当前线程并且放弃当前的锁。然后，另一个线程获取了同一个锁，然后出发了`Object.notifyAll`方法来通知所有在等待锁的线程当前的事件发生。

{% highlight java %}
public synchronized notifyJoy() {
    joy = true;
    notifyAll();
}
{% endhighlight %}


```
notify vs notifyAll: notify后，只有一个线程会被唤醒。所有线程需竞争锁来被唤醒，因此不能确定那个线程会被执行。notifyAll后，所有在等待锁的线程都会被唤醒。
```

生产者消费者模式是一个典型的Guarded Block的应用场景。一下是一个简易的生产者消费者实现。

{% highlight java %}
public class Drop {
    // Message sent from producer
    // to consumer.
    private String message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;

    public synchronized String take() {
        // Wait until message is
        // available.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = true;
        // Notify producer that
        // status has changed.
        notifyAll();
        return message;
    }

    public synchronized void put(String message) {
        // Wait until message has
        // been retrieved.
        while (!empty) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store message.
        this.message = message;
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
{% endhighlight %}

生产者：

{% highlight java %}
import java.util.Random;

public class Producer implements Runnable {
    private Drop drop;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        String importantInfo[] = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
        };
        Random random = new Random();

        for (int i = 0;
             i < importantInfo.length;
             i++) {
            drop.put(importantInfo[i]);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {}
        }
        drop.put("DONE");
    }
}
{% endhighlight %}

消费者：
{% highlight java %}
import java.util.Random;

public class Consumer implements Runnable {
    private Drop drop;

    public Consumer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        for (String message = drop.take();
             ! message.equals("DONE");
             message = drop.take()) {
            System.out.format("MESSAGE RECEIVED: %s%n", message);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {}
        }
    }
}
{% endhighlight %}

Main：
{% highlight java %}

public class ProducerConsumerExample {
    public static void main(String[] args) {
        Drop drop = new Drop();
        (new Thread(new Producer(drop))).start();
        (new Thread(new Consumer(drop))).start();
    }
}
{% endhighlight %}

以上这个例子虽然使用了两个Thread但并没有并发的完成任务。当生产者读取信息的时候，消费者只能等待生产者完成任务。而当消费者对一个信息处理时，生产之也只能等待消费者完成任务。因此并没有真正做到并发的处理问题。当生产者和消费者所要处理的任务都十分消耗资源时，这样的运行方式就变得十分浪费和效率底下。[这里](https://dzone.com/articles/producer-consumer-pattern)描述了生产者消费者的另一种通过LinkedBlockingQueue实现的方法。