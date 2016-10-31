---
layout: post
title:  "代理模式 - proxy pattern"
date:   2016-10-05 15:12:25
author:     "Yan"
header-img: "img/post-bg-2015.jpg"
catalog: true
comments: true
tags:
    - design patern
---
原文链接 [http://www.oodesign.com/proxy-pattern.html](http://www.oodesign.com/proxy-pattern.html)

# 动机

我们有时候需要控制一个对象的访问权。比如说，当我们需要从一些庞大对象上只调用几个方法的时候，我们就不得不先完整的构建这些对象。在这种时候，我们就需要使用一些轻量级的对象的接口来暴露那些重量级的对象的方法。这些轻量级的对象就叫做代理（proxies）。当我们需要那些重量级的对象时，我们会用这些轻量级的对象来保存和构建那些重量级的对象，并直接使用这些轻量级的对象所暴露的接口来调用所代理的重量级的对象的方法。

我们需要控制一个对象的访问权的原因有以下几点：
1. 控制一个重量级对象的实例化和初始化。
2. 给予一个对象不同的访问权
3. 当一个对象被不同的进程或者甚至不同物理机访问或者调用时，代理模式可以提供一种安全，成熟的调用和访问的方式。

以一个图片预览的程序为例，图片预览程序必须可以列出并且打开在这一个文件夹中的高分辨率的图片。可是，使用这个程序浏览文件夹的用户不会经常把所有的图片都预览过的。有时候用户可能只是寻找一个特定的图片或者只是一个图片的名字。所以我们会需要图片预览程序可以列出所有的图片对象并且只有在需要渲染图片的时候才把特定的图片对象读取到内存中。

# 目的

代理模式的目的就是提供控制对象的占位符（placeholder）。

实现

下图为代理模式的UML class diagram￼

![Image of Factory Pattern]({{ site.url }}/img/delegate-pattern-1.png)

上图中，代理模式用到的类有：

**Subject** － 这个接口由RealSubject实现来表示接口的服务。为了代理RealSubject，并可以用Proxy类替代任何RealSubject类，代理类Proxy也必须实现Subject接口。

**Proxy**
1. 储存一个可以使Proxy类访问RealSubject的引用
2. 为了可以随意替换RealSubject类的使用，Proxy类必须实现与RealSubject类同样的接口
3. 有对RealSubject类的控制访问权，可以控制RealObject对象的创建和删除
4. 根据代理具体的需求，还可以有别的方法

**RealSubject** － 代理类所代理的真正对象

# 描述

用户持用代理（proxy）的引用来操作真正对象。比如，根据上图，用户可以通过Proxy来调用doSomething()。此时，Proxy对象也可以先处理其它的事情。用户也可以建立新的RealObject对象来初始化，检查用户的对方法调用的权限，比如说，改变引用对象的值。

# 应用和实例

需要代理模式的一般场景可以是需要控制访问对象，或者是需要复杂逻辑的对象引用。以下四例是典型的代理模式的需求场景：

1. **虚拟代理**：推迟重量级对象的建立和初始化直到我们需要的时候。此时，对象的建立是根据需求的。（比如说，当我们需要doSomething（）方法时才去建立RealObject对象）。
2. **远程代理**：为不同空间地址（address space）的对象建立一个本地的代理。最常见的例子就是Java RMI stub Objects。 Stub Object 扮演代理的角色。 当我们调用Stub Object的方法时，Stub会连接并且调用远程的对象上的方法。
3. **防护代理**：比如说，通过Proxy类来控制对RealSubjet的方法的调用。我们可以在Proxy类里定义不同对象对RealObject类的访问权限。
4. **智能引用**：我们可以通过代理模式来智能实现对对象的CRUD。比如说，通过线程池中线程对数量来决定是否增加或者删除线程，或者是在orm中，决定是否从数据库里把数据读进内存。

# 实例 － 虚拟代理实例

试想一个图片预览程序，这个程序可以列出文件夹中的所有图片，也可以展示高分辨率的图片。当用户没有选中特定的图片时，程序只需列出所有图片。当用户选中特定图片的时候，程序才需把图片渲染出来。
下图为这个小程序的类图。

![Image of Factory Pattern]({{ site.url }}/img/delegate-pattern-2.png)

以下为Image 接口，这个接口有一个showImage（）方法。调用这个方法可以渲染指定的图片。

{% highlight java %}

   package proxy;
    /**
     * Subject Interface
     */
    public interface Image {

        public void showImage();
        
    }

{% endhighlight %}

下列代码是Proxy类的实现。图片代理是一个虚拟代理。虚拟代理类可以建立或者读区一个指定的图片对象。所以，当我们不需要渲染图片的时候，这个虚拟代理Proxy类节省了读取图片进入内存的消耗：

{% highlight java %}
package proxy;

/**
 * Proxy
 */
public class ImageProxy implements Image {

    /**
     * Private Proxy data 
     */
    private String imageFilePath;
    
    /**
     * Reference to RealSubject
     */
    private Image proxifiedImage;
    
    
    public ImageProxy(String imageFilePath) {
        this.imageFilePath= imageFilePath;  
    }
    
    @Override
    public void showImage() {

        // create the Image Object only when the image is required to be shown
        
        proxifiedImage = new HighResolutionImage(imageFilePath);
        
        // now call showImage on realSubject
        proxifiedImage.showImage();
        
    }

}

{% endhighlight %}

以下的代码是RealSubject的实现。这个类会生成一个具体的，重量级的，实现了Image接口的对象。这个HighResolutionImage类会从硬盘中读取这个高分辨率的图片，然后通过showImage（）方法渲染到屏幕上。

{% highlight java %}

package proxy;

/**
 * RealSubject
 */
public class HighResolutionImage implements Image {

    public HighResolutionImage(String imageFilePath) {
        
        loadImage(imageFilePath);
    }

    private void loadImage(String imageFilePath) {

        // load Image from disk into memory
        // this is heavy and costly operation
    }

    @Override
    public void showImage() {

        // Actual Image rendering logic

    }

}
{% endhighlight %}

下列代码是一个简单的图片预览程序。这个图片一开始加载了三个图片但只渲染了一张图片。然后渲染其中一张，一次使用了代理模式另一次直接渲染。需要注意的是，虽然三张图片都已经被加载了，但图片只有在需要的时候才会被加载到内存。当我们没有使用代理模式的时候，三张图片都加载到了内存里即使只有一个图片真正需要渲染。

{% highlight java %}

package proxy;

/**
 * Image Viewer program
 */
public class ImageViewer {

    
    public static void main(String[] args) {
        
    // assuming that the user selects a folder that has 3 images    
    //create the 3 images   
    Image highResolutionImage1 = new ImageProxy("sample/veryHighResPhoto1.jpeg");
    Image highResolutionImage2 = new ImageProxy("sample/veryHighResPhoto2.jpeg");
    Image highResolutionImage3 = new ImageProxy("sample/veryHighResPhoto3.jpeg");
    
    // assume that the user clicks on Image one item in a list
    // this would cause the program to call showImage() for that image only
    // note that in this case only image one was loaded into memory
    highResolutionImage1.showImage();
    
    // consider using the high resolution image object directly
    Image highResolutionImageNoProxy1 = new HighResolutionImage("sample/veryHighResPhoto1.jpeg");
    Image highResolutionImageNoProxy2 = new HighResolutionImage("sample/veryHighResPhoto2.jpeg");
    Image highResolutionImageBoProxy3 = new HighResolutionImage("sample/veryHighResPhoto3.jpeg");
    
    
    // assume that the user selects image two item from images list
    highResolutionImageNoProxy2.showImage();
    
    // note that in this case all images have been loaded into memory 
    // and not all have been actually displayed
    // this is a waste of memory resources
    
    }
        
}

{% endhighlight %}

# 工业实例 － Java Remote Method Invocation （RMI）

在java RMI 中，一个JVM中的对象叫做一个client。这个client可以用来调用在另一个JVM中的对象。第二个对象就叫做remote object。代理（proxy）也叫stub是在用户机（client）器上的。client可以调用代理就像调用对象本身。这个代理和那个RealSubject实现了同样的接口。代理proxy会处理调用远程对象的交流并调用其方法，最后如果有的话会返回结果给client。这里，这个代理proxy就是一个远程代理。

**相关的模式**

1. Adapter Design pattern
2. Decorator Design pattern