---
layout: post
title:  "Using Java Regular Expression"
date:   2015-03-23 10:00:00
author:     "Yan"
header-img: "img/post-bg-2015.jpg"
catalog: true
tags:
    - regular expression
    - java
---

In this post, I want to present a simple use of [Java Regular Expression](http://docs.oracle.com/javase/tutorial/essential/regex/).

Java regular expression is a very powerful tool for manipulating Strings. The usage can be found in many field (e.g. String Tokeniser, NLP).

To start using regular expression, I recommand firstly understanding what is Regular Expression and find a [regular expression cheat sheet](http://www.rexegg.com/regex-quickstart.html). Then, it worth to try out by using Java regular expression library.

Here is an expamle, the example is from [http://www.javapractices.com/topic/TopicAction.do?Id=87](http://www.javapractices.com/topic/TopicAction.do?Id=87)

{% highlight java %}
import java.util.regex.*;

public final class RegularExpressions {

  /**
  * The pattern is matched to the first argument.
  */
  public static void main (String... aArguments) {
    matchParts(aArguments[0]);
    matchAll(aArguments[0]);
  }

  /**
  * The Matcher.find method attempts to match *parts* of the input
  * to the given pattern.
  */
  private static void matchParts(String aText){
    log(fNEW_LINE + "Match PARTS:");
    //note the necessity of the comments flag, since our regular
    //expression contains comments:
    Pattern pattern = Pattern.compile(fREGEXP, Pattern.COMMENTS);
    Matcher matcher = pattern.matcher(aText);
    while (matcher.find()) {
      log("Num groups: " + matcher.groupCount());
      log("Package: " + matcher.group(1));
      log("Class: " + matcher.group(2));
    }
  }

  /**
  * The Matcher.matches method attempts to match the *entire*
  * input to the given pattern all at once.
  */
  private static void matchAll(String aText){
    log(fNEW_LINE + "Match ALL:");
    Pattern pattern = Pattern.compile(fREGEXP, Pattern.COMMENTS);
    Matcher matcher = pattern.matcher(aText);
    if(matcher.matches()) {
      log("Num groups: " + matcher.groupCount());
      log("Package: " + matcher.group(1));
      log("Class: " + matcher.group(2));
    }
    else {
      log("Input does not match pattern.");
    }
  }

  //PRIVATE

  private static final String fNEW_LINE = System.getProperty("line.separator");
  
  private static void log(String aMessage){
    log(aMessage);
  }

  /**
  * A commented regular expression for fully-qualified type names which
  * follow the common naming conventions, for example, "com.myappBlah.Thing".
  *
  * Thus, the "dot + capital letter" is sufficient to define where the
  * package names end.
  *
  * This regular expression uses two groups, one for the package, and one
  * for the class. Groups are defined by parentheses. Note that ?: will
  * define a group as "non-contributing"; that is, it will not contribute
  * to the return values of the <tt>group</tt> method.
  * 
  * As you can see, regular expressions are often cryptic.
  */
  private static final String fREGEXP =
    "#Group1 - Package prefix without last dot: " + fNEW_LINE +
    "( (?:\\w|\\.)+ ) \\." + fNEW_LINE +
    "#Group2 - Class name starts with uppercase: " + fNEW_LINE +
    "( [A-Z](?:\\w)+ )"
  ;
} 

{% endhighlight %}

The above example is trying to use Java Regular example library to parse the package path such as java.lang.String. To understand the regular expression ```( (?:\\w|\\.)+ ) \\.( [A-Z](?:\\w)+ )```, we should firstly find out the pattern of the package path. Java package path is start with a small word followed by one or more word as the root level name. Then followed by a dot which is the path separator. Then there are one or more directory which is same structure as we described before. Therefore, we can get the first group of our regular expression:

{% highlight java %}
( (?:\\w|\\.)+ ) \\. // pattern - package path
{% endhighlight %}

The ```((?:\\w|\\.)+)``` will catch the package name in the group and the path separator ```.``` will be omitted. 

Secondly, we need parse the class name. As Java class name is all start in captial as part of java name convention, we will parse all the java class name as a captial word followed by any other words. To be noticed, ```\\w``` is the shortage of ```[a-z][A-Z]```. Therefore, we get the pattern:

{% highlight java %}
( [A-Z](?:\\w)+ ) // pattern - class name
{% endhighlight %}

The ```group``` in java ```Pattern.class``` is great idea to get the value from a string. Each group is in between of extra round brackets. In the above example, we can view the pattern of ```( (?:\\w|\\.)+ ) \\.( [A-Z](?:\\w)+ )``` as (group1: pattern - package path without "." ).(group2: pattern - class name). 

There are few tips for newbie:

1. in Java, to use the regular expression such as \w, **you have to use a extra backslash for it ```\\w```**. The reson is that '\' is a Java reserved character for String.class (e.g. "\n", "\t", "\"). However, we want the string to be present like '\w'. Therefore, we use a backslash to escape the special character '\' itself. We will write something like ```"\\w\\W\\D"``` in our regular expression string. To use '\' in regular expression, it has to be ```"\\\\"```. Therefore, when you see a pattern like "https:\\\\\\\\(\\w)+". Don't panic, this's trying to parse a url pattern.

2. to use the group, you have to call the method ```matches()``` on your Matcher object. Otherwise, you will found a error says the group is not found when using ```mathcer.group(\*your group number here*\)```.


![regex]({{ site.url }}/img/regex.png)
