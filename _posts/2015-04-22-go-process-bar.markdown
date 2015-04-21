---
layout: post
title:  "A Simple Commandline Process Bar by Golang"
date:   2015-04-22 10:00:00
categories: go
---

I saw a question people asked which is how to make a commandline process bar by Golang.

Here is a simple one I did.

{% highlight go %}

package main

import (
    "fmt"
    "strconv"
    "time"
)

func main() {
    for i := 10; i <= 100; i += 10 {
        str := "[" + bar(i/5, 20) + "] " + strconv.Itoa(i) + "%"
        fmt.Printf("\r%s", str)
        time.Sleep(1 * time.Second)
    }
    fmt.Println()
}

func bar(count, size int) string {
    str := ""
    for i := 0; i < size; i++ {
        if i < count {
            str += "="
        } else {
            str += " "
        }
    }
    return str
}


{% endhighlight %}

The result will look like this:

![sample_bar]({{ site.url }}/assets/wdpu2A.gif)