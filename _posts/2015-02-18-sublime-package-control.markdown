---
layout: post
title:  "Package Control not showing"
date:   2015-02-18 10:31:00
categories: general
---
Yesterday, when I am using my sublime and trying to install a new package, I experience an terrible issue. That is my package control is not showing in the list when I press `ctrl + shift + p`. I tried remove the `control.sublime` from both Pristine Packages and Installed Packages folder. But this is not solving my problem. 

Until I saw the [comment by JustANull (commented on 27 Mar 2012)](https://github.com/wbond/package_control/issues/112). I followed the suggestion to go to the Preference -> Settings-User and removed the `package control` from the "ignored_packages" list and restart the sublime. Bang! The package control is back~ 

Happy Chiniese New Year