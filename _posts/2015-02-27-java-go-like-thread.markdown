---
layout: post
title:  "A Simple Goroutine Like Java Implementation "
date:   2015-02-27 16:00:00
categories: java
---

One of the greatest aspect of golang is that it is easy to do concurrent thing by using goroutine. In Java, when we want to start a thread, we have to define a class and implements the relative method. Here is a simple implementation in Java which can simplify the concurrent process:

{% highlight java linenos %}


package com.learnconcurrent;

/**
 * Created by daiyan on 27/02/15.
 */
public class MultiThread
{
    private class FuncThread implements Runnable
    {

        private Func func;

        FuncThread(Func func)
        {
            this.func = func;
        }

        @Override
        public void run()
        {
            func.func();
        }


        void start()
        {
            new Thread(new FuncThread(func)).start();
        }

    }

    private abstract class Func
    {

        abstract void func();
    }

    private void foo()
    {
        new FuncThread(new Func()
        {
        	// you can implement your own method
            @Override
            void func()
            {
                System.out.println("print a foo");
            }
        }).start();
    }

    public static void main(String[] args)
    {
        new MultiThread().foo();
    }

}



{% endhighlight %}


At line 40, we can simply start a thread. This implementation can be similar with those implemenation that are trying bring Functional Programming into Java.


![gopherswrench]({{ site.url }}/assets/gopherswrench.jpg)


