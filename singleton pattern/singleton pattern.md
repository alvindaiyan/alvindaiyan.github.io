<h2>Intro</h2>
<p>
Singleton pattern is to have only one instance for a class. One of the most important reason to use singleton pattern is to save memory. For example, in a system there should be only one window manager. Singletons is good at providing a centralized management system for internal or external resources. 
<p>

	
<h2>Description</h2>

<p>The class can create no more than one instace. All the programs want to access the instance will use the same reference.</p>


<h2>Intent</h2>
<ul>
	<li>No more than one instance should be created globally,</li>
	<li>Provide a global access point. 
</ul>


<h2>UML</h2>
 <img src="https://github.com/alvindaiyan/learnDesignPattern/blob/master/singleton%20pattern/singleton%20diagram.png" alt="singleton img" />


<h2>Implementation</h2>
<h3>Java</h3>
<pre>
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
</pre>	
<p>
The implementations above in Java should be robust for any situtation. However, synnchronize a function is expensive. 
</p>

<h3>Go</h3>
<p> example from <a href="http://stackoverflow.com/questions/1823286/singleton-in-go">stackoverflow</a></p>

<pre>
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
</pre>
<p>
The above code is an implementation of Singleton Pattern in Go. However, Go is not a class-based OO language. Singleton pattern is less useful for Go compared with Java.
</p>
