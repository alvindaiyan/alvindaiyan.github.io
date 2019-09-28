---
layout: post
title:  "利用ss为git clone提速"
date:   2019-06-23 21:00:00
author:     "Yan"
header-img: "img/the_dark_times_are_coming_by_msdudettes-d5m99ns.jpg"
catalog: true
comments: true
tags:
    - devops
---

国内工作的时间成本越来越高的前提下，只要超过10%的效率提高都是十分值得的。其中一个就是如何面对日趋恶劣的网络环境。
想当年在国外从GitHub上git clone项目时5mb/s的速度和当下几十kb的网速时就百(十)感(分)交(蛋)集(疼)。

好了，废话不多说了，如果你用ss搭了科学上网的梯子，并且是一个开发，那么就一定要为你的git提个速。

# 首先，我们需要搞清楚ss对socks5的监听端口：

![socks5_1.png]({{ site.url }}/img/socks5_1.png)

![socks5_2.png]({{ site.url }}/img/socks5_2.png)

# 接下来，我们需要设置一下git

```
git config --global http.proxy 'socks5://127.0.0.1:1086'
git config --global https.proxy 'socks5://127.0.0.1:1086'
```


# 最后，我们对比一下结果

## 提速前

![socks5_before.png]({{ site.url }}/img/socks5_before.png)

## 提速后

![socks5_result.png]({{ site.url }}/img/socks5_result.png)


写在最后，今天心情贼差，实在看不下去那么几kb，几kb的蹦了。

