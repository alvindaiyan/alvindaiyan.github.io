---
layout: post
title:  "Using Java Reflection to mock an object"
date:   2015-11-10 10:00:00
author:     "Yan"
header-img: "img/post-bg-2015.jpg"
catalog: true
tags:
    - java
    - reflection
---

By doing this simple program, we can learn how to use Java reflection and mock a object with Google gson annotation.



## The code ##

{% highlight java %}

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan.dai on 3/11/2015.
 */
public class MockObject
{
    public static <T> T fakeObject(Class<T> cls) throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        Constructor<T>[] constructors = (Constructor<T>[]) cls.getDeclaredConstructors();
        Constructor<T> constructor = null;
        for (Constructor<T> c : constructors)
        {
            if (c.getParameterCount() == 0)
            {
                constructor = c;
                break;
            }
        }

        if (constructor == null)
        {
            throw new InstantiationException("cannot find default constructor for class: " + cls.getName());
        }
        constructor.setAccessible(true);

        T mock = constructor.newInstance();
        for (Field field : cls.getDeclaredFields())
        {
            field.setAccessible(true);

            if (field.getDeclaredAnnotations() != null &&
                    field.getDeclaredAnnotations().length > 0 &&
                    field.getDeclaredAnnotations()[0].annotationType().getName().equals("com.google.gson.annotations.Expose")
            )
            {
                if (field.getType().isAssignableFrom(String.class))
                {
                    field.set(mock, "string");
                }
                else if (field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(double.class))
                {
                    field.set(mock, 1.1);
                }
                else if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class))
                {
                    field.set(mock, 0);
                }
                else if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class))
                {
                    field.set(mock, 0L);
                }
                else if (field.getType().isAssignableFrom(List.class))
                {
                    List<Object> list = new ArrayList<>();
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Object element = mockPrimitiveType(genericType);
                    if(element == null)
                    {
                        element = fakeObject(genericType);
                    }
                    list.add(element);
                    list.add(element);
                    field.set(mock, list);
                }
                else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class))
                {
                    field.set(mock, false);
                }
                else if (field.getType().isArray())
                {
                    Class<?> elementType = field.getType().getComponentType();
                    if (elementType.equals(Character.class) || elementType.equals(char.class))
                    {
                        System.err.println("java reflection is sick");
                    }
                    else
                    {
                        Object array = Array.newInstance(elementType, 2);
                        Array.set(array, 0, fakeObject(elementType));
                        Array.set(array, 1, fakeObject(elementType));
                        field.set(mock, array);
                    }
                }
                else
                {
                    Object ins = fakeObject(field.getType());
                    field.set(mock, ins);
                }
            }
        }
        return mock;
    }


    private static Object mockPrimitiveType(Class<?> cls)
    {
        if (cls.isAssignableFrom(String.class))
        {
            return "string";
        }
        else if (cls.isAssignableFrom(Double.class) || cls.isAssignableFrom(double.class))
        {
            return 1.1;
        }
        else if (cls.isAssignableFrom(Integer.class) || cls.isAssignableFrom(int.class))
        {
            return 0;
        }
        else if (cls.isAssignableFrom(Long.class) || cls.isAssignableFrom(long.class))
        {
            return 0L;
        }
        return null;
    }
}


{% endhighlight %}


## The testing program: ##

{% highlight java %}

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by yan.dai on 3/11/2015.
 */
public class MyObj
{
    @Expose
    private String name;
    @Expose
    private int age;
    @Expose
    private List<String> values;

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        MyObj configs = MockObject.fakeObject(MyObj.class);
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();
        System.out.println(gson.toJson(configs));
    }
}

{% endhighlight %}


## result ##

```{"name":"string","age":0,"values":["string","string"]} ```
