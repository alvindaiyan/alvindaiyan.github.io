---
layout: post
title:  "Java并发回炉再造之Oracle文档四"
date:   2018-06-14 15:44:00
author:     "Yan"
header-img: "img/shan.jpg"
catalog: true
comments: true
tags:
    - java
---

原文: [https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)

# Immutable Objects

当一个对象在被构造之后，它的状态不能被改变，这样的对象就叫不可变对象。不可变对象在并发问题中是被广泛认为是安全的，因为其状态在被构造之后不可变，因此线程不能对其状态进行改变由此确保了对象的状态在所有线程间是统一的。

在每次使用或更新不可变对象时，我们需要重新创建对象。虽然这听起来有点浪费，但是不可变对象带来的在并发环境中的优势是不可替代的。特别是在今天，Java的gc高度精确和有效的情况下，创建对象的开销和不可变对象所带来的在并发环境中的优势已经可以忽略不计。

## A Synchronized Class Example

SynchronizedRGB.class代表了一个色块。

The class, SynchronizedRGB, defines objects that represent colors. Each object represents the color as three integers that stand for primary color values and a string that gives the name of the color.

{% highlight java %}

public class SynchronizedRGB {

    // Values must be between 0 and 255.
    private int red;
    private int green;
    private int blue;
    private String name;

    private void check(int red,
                       int green,
                       int blue) {
        if (red < 0 || red > 255
            || green < 0 || green > 255
            || blue < 0 || blue > 255) {
            throw new IllegalArgumentException();
        }
    }

    public SynchronizedRGB(int red,
                           int green,
                           int blue,
                           String name) {
        check(red, green, blue);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.name = name;
    }

    public void set(int red,
                    int green,
                    int blue,
                    String name) {
        check(red, green, blue);
        synchronized (this) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.name = name;
        }
    }

    public synchronized int getRGB() {
        return ((red << 16) | (green << 8) | blue);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void invert() {
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;
        name = "Inverse of " + name;
    }
} 
{% endhighlight %}

虽然set，get和invert都写在了synchronized代码块中，我们仍然不能保证SynchronizedRGB在所有线程中的操作是线程安全的。比如一下这个例子：

Thread1: set(1, 2, 3, 'from thread1') | Thread2: set(1, 2, 3, 'from thread2') | getName() == 'thread2' (but expect thread1)

以下代码确保了color在不同线程间的状态统一。

{% highlight java %}
synchronized (color) {
    int myColorInt = color.getRGB();
    String myColorName = color.getName();
} 
{% endhighlight %}

## A Strategy for Defining Immutable Objects


