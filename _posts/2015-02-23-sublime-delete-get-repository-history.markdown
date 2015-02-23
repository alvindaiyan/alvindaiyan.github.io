---
layout: post
title:  "Delete A Git Repository History"
date:   2015-02-23 15:00:00
comments: true
categories: general
---

Sometimes, you have to delete some of the history of your repository in your github. Some reason like, you have some confidential detail negligently submitted into your public repository (e.g. personal password). Then you cannot just update the file because these detail is still available to others by viewing your file history. 

The way to delete these history is:
 1. go to your git hub, settings -> delete repository.
 2. create a new one with same name.
 3. update you local repository and commit to the new one (if the new repo is same name)

 As a newbie for github, please remember never have any confidential detail sumbmitted:)
 