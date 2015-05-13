---
layout: post
title:  "Java Generics: PECS principle"
date:   2015-05-14 10:00:00
categories: java
---

Yesterday, I read about PECS principle from the book [effective java](http://www.amazon.com/Effective-Java-Edition-Joshua-Bloch/dp/0321356683). In this post, I want to record my current understanding about this PECS principle by using the ArrayList source code from JDK 1.8.

PECS stands for Producer extends, Consumer super. This is a general principle when applying wild card of Java Generics. 
By saying a producer for a generic method, this is trying to imply that the element is giving its element to compute. For example, the ```addAll``` method in ```ArrayList.java```:

{% highlight java %}
/**
* Appends all of the elements in the specified collection to the end of
* this list, in the order that they are returned by the
* specified collection's Iterator.  The behavior of this operation is
* undefined if the specified collection is modified while the operation
* is in progress.  (This implies that the behavior of this call is
* undefined if the specified collection is this list, and this
* list is nonempty.)
*
* @param c collection containing elements to be added to this list
* @return <tt>true</tt> if this list changed as a result of the call
* @throws NullPointerException if the specified collection is null
*/
public boolean addAll(Collection<? extends E> c) {
    Object[] a = c.toArray();
    int numNew = a.length;
            ensureCapacityInternal(size + numNew);  // Increments modCount
            System.arraycopy(a, 0, elementData, size, numNew);
            size += numNew;
            return numNew != 0;
        }

{% endhighlight %}

The ```addAll``` method simply take a collection ```c``` and have all the elements in ```c``` added to the current arrayList. It will first convert the collection ```c``` to a ```Object[]``` a. And copy all the elements in a to the current arrayList. Therefore, we can found that the elements the ```addAll``` method used is produced by ```c``` because ```c``` is converted to ```a``` and a is used to perform the array copying method. Here, c is a producer and we need the element in c to be child to c. We use ```<? extends E>```.

In opposite, a consumer is to provide a mechanism or function to execute. Here is an example from JDK 1.8 in ```ArrayList.java```:
{% highlight java %}

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final E[] elementData = (E[]) this.elementData;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

{% endhighlight %}

This method is overriding the ```Iterable.interface```. The action is a consumer to the method. The action provide an actual implementation of the ```accept(Object)```. For each element in the current arrayList, they are going to be performed the logic in the ```action.accept(Object)```. Therefore, the action is a consumer and ```<? super E>``` is being used as the parameter for the ```forEach()``` has to provide an implementation of ```accept()```.

Above is my current understanding of PECS principle.
