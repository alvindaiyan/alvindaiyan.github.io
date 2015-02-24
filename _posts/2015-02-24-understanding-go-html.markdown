---
layout: post
title:  "Simple Tutorial of Go html parser"
date:   2015-02-24 14:21:00
categories: go
---

Parsing html document is one of very useful feature for web crawler when you only need crawl a specific element content on the web page. In go 1.4.0, a html parser in [golang.org/x/net/html](https://godoc.org/golang.org/x/net/html). In this post, I will state how to use the "html" library to parse a html document.


![github]({{ site.url }}/assets/gohtml.jpg)

As my computer still using go 1.3.1, there is no such library. So I have to install the library manually by (run by command line):

``` go get golang.org/x/net/html ```

Now, lets get a html parse object in go.
 
1. **Read the file.** Depending on the source, there are many different ways. In this post, I will use the html from Internet. What you need to do is to download the page by: 

{% highlight go %}
package htmlparser

import (
	"log"
	"net/http"
	"io/ioutil"
)


func DowloadByUrl(url string) []byte {
	log.Println("download start download url: ", url)
	response, err := http.Get(url)
	log.Println("download finished url: ", url)

	if err != nil {
		log.Printf("%s", err)
	} else {
		defer response.Body.Close()
		contents, err := ioutil.ReadAll(response.Body)
		if err != nil {
			log.Printf("%s", err)
		}
		log.Printf("%s\n", string(contents))
		return contents
	}
	return nil
}

{% endhighlight %}

In this piece of code, the html will be downloaded regarding of the given url and return its content as a byte array. The code of ```string(contents)``` is used to convert the byte array to a string. If there is an error occured when downloading the web content, the error will be record and a [nil](http://stackoverflow.com/questions/4217864/null-value-in-golang) is returned.<br/><br/>
2. **Turn a byte array to a [\*Reader](http://golang.org/pkg/bytes/#Reader).** In order to turn the byte array to a reader:

{% highlight go %}

	import "bytes"

	...
	reader := bytes.NewReader(/*your byte array here*/)
	...

{% endhighlight %}
Here now, we can get a object for parsing.<br/><br/>
3. **Parse the document to [\*html.Node](https://www.godoc.org/golang.org/x/net/html#Node).** The code is following:

{% highlight go %}

	import "golang.org/x/net/html"

	...
	doc, err := html.Parse(reader)
	if err != nil {
		log.Fatal(err)
	}
    	...

{% endhighlight %}
Now, we have a parsed object ```doc```.