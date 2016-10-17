---
layout: post
title:  "Delete A Git Repository History"
date:   2015-02-23 15:00:00
author:     "Yan"
header-img: "img/post-bg-2015.jpg"
catalog: true
comments: true
tags:
    - git
---

Sometimes, you have to delete some of the history of your repository in your github. Some reason like, you have some confidential detail negligently submitted into your public repository (e.g. personal password). Then you cannot just update the file because these detail is still available to others by viewing your file history. 

![github]({{ site.url }}/img/github.png)

The way to delete these history is:

- go to your git hub, settings -> delete repository.
-  create a new one with same name.
- update you local repository and commit to the new one (if the new repo is same name)

 As a newbie for github, please remember never have any confidential detail sumbmitted:)
 
