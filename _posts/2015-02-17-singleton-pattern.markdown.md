---
layout: post
title:  "Singleton Pattern Learning"
date:   2015-02-17 10:36:00
categories: design_pattern
---

Singleton pattern is to have only one instance for a class. One of the most important reason to use singleton pattern is to save memory. For example, in a system there should be only one window manager. Singletons is good at providing a centralized management system for internal or external resources. 


	
## Description
The class can create no more than one instace. All the programs want to access the instance will use the same reference.


## Intent
- No more than one instance should be created globally,
- Provide a global access point. 


## UML
 ![singleton img]({{ site.url }}/assets/singleton%20diagram.png)


## Implementation

### Java

{% highlight java %}

	public class Singleton
	{
		private static Singleton instance;

		private Singleton(){}

		 // synchronized keyword is important since 
		 // it can help avoid multiple threads create different instance at same time
		public static synchronized Singleton getInstance()
		{
			if(instance = null)
			{
				instance = new Singleton();
			}
			return instance;
		}	
	}	

{% endhighlight %}

The implementations above in Java should be robust for any situtation. However, synnchronize a function is expensive. 

### Go

> example from [stackoverflow](http://stackoverflow.com/questions/1823286/singleton-in-go)

{% highlight go %}

	package singleton

	import "sync"

	type singleton struct {}

	var instance *singleton
	var once sync.Once

	func New() *singleton {
		if instantiated == nil {
			once.Do(func() {		
				instantiated = new(single)		
			})
		}
		return instantiated
	}
{% endhighlight %}


The above code is an implementation of Singleton Pattern in Go. However, since Go is not a class-based OO language, most of the classic OO design patterns are awkward to translate into Go. Therefore, Singleton pattern is less useful for Go compared with Java.