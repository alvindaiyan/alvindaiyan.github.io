---
layout: post
title:  "Vuejs 3 初探"
date:   2019-09-28 22:16:00
author:     "Yan"
header-img: "img/the_dark_times_are_coming_by_msdudettes-d5m99ns.jpg"
catalog: true
comments: true
tags:
    - devops
---

今天恰好有个小项目要重构一下之前一个前端项目，决定重新熟悉一下Vuejs。之前用过一些vuejs 1.x和2.x，趁着次机会
熟悉一下vuejs 3。

# TL；DR

我不是一个专业的前端开发，所以这篇博客会显得非常浅薄或者出现一些幼稚的错误，请谅解。

# 重新安装/升级本地依赖

我本地之前安装的是nvm，所以先选择vuejs 3适用的npm和nodejs环境：

```
➜  Dev nvm use v10.0.0
Now using node v10.0.0 (npm v5.6.0)
➜  Dev vue --version
2.8.2
➜  Dev node --version
v10.0.0
```

接着，安装vue-cli：

```
➜  Dev npm install -g @vue/cli
...
+ @vue/cli@3.11.0
added 910 packages in 259.876s
```

安装成功后，查看vue是否是正确的版本：

```
➜  Dev vue --version
2.8.2
```

hmmm，不太对，我想要装最新的vuejs 3，不应该是2.x，估计是之前安装过vuejs在这个环境？所以我决定把vue删掉，重装

```
➜  Dev npm uninstall vue-cli -g
up to date in 0.036s
```

重装vuejs后，在查看vue的版本：

```
➜  Dev vue --version
2.8.2
```

还是不对。。。丧心，是不是nvm的环境有问题？

```
➜  Dev which vue
/Users/yan/.npm-packages/bin/vue
```

果然，这里，vue不应该指向是我的npm-packages里，应该是在nvm的npm-packages里。先简单的重新通过nvm装载一下npm环境，然后再看看vue的地址对不对：

```
➜  Dev nvm use default
Now using node v8.9.1 (npm v5.5.1)
➜  Dev nvm use v10.0.0
Now using node v10.0.0 (npm v5.6.0)
➜  Dev which vue
/Users/yan/.nvm/versions/node/v10.0.0/bin/vue
```

这次对了，至于为什么出现这个错误，我决定好像并不重要。再次查看vue的版本，现在也对了：

```
➜  Dev vue --version
3.11.0
```

# 创建Vuejs 3项目

首先是创建项目：

```
➜  Dev vue create super-vue
?  Your connection to the default npm registry seems to be
slow.
   Use https://registry.npm.taobao.org for faster installat
ion? Yes


Vue CLI v3.11.0
? Please pick a preset: default (babel, eslint)


Vue CLI v3.11.0
✨  Creating project in /Users/yan/Dev/supermarkets/super-vue.
🗃  Initializing git repository...
⚙  Installing CLI plugins. This might take a while...


> fsevents@1.2.9 install /Users/yan/Dev/supermarkets/super-vue/node_modules/fsevents
> node install

[fsevents] Success: "/Users/yan/Dev/supermarkets/super-vue/node_modules/fsevents/lib/binding/Release/node-v64-darwin-x64/fse.node" is installed via remote

> yorkie@2.0.0 install /Users/yan/Dev/supermarkets/super-vue/node_modules/yorkie
> node bin/install.js

setting up Git hooks
done


> core-js@2.6.9 postinstall /Users/yan/Dev/supermarkets/super-vue/node_modules/core-js
> node scripts/postinstall || echo "ignore"

added 1236 packages in 30.263s
🚀  Invoking generators...
📦  Installing additional dependencies...

added 35 packages, updated 2 packages and moved 9 packages in 15.67s
⚓  Running completion hooks...

📄  Generating README.md...

🎉  Successfully created project super-vue.
👉  Get started with the following commands:

 $ cd super-vue
 $ npm run serve
```

新的vue-cli脚手架真的简化了很多流程，我记得之前的vue-cli要选测试框架，选es6，选jslint，好多好多。现在就是选一个基础款就行了。而且还把taobao的源给配置了，简直太照顾我们这些二把刀了。

进入到项目，不用跑```npm install```了，直接跑```npm run serve```就可以了。

# 简单的配置工具和框架

由于项目比较小，暂时还没有用到vue-router，所以等未来有用到在加。

国内比较流行的前端框架是antd，elementjs两个，一个来自阿里，一个来自饿了么的前端团队。但我选vuetify。先安装vuetify依赖，从官网上我们看到了vuetify和vuejs做了很好的融合，所以安装很简单：

```
➜  super-vue git:(master) vue add vuetify

📦  Installing vue-cli-plugin-vuetify...

+ vue-cli-plugin-vuetify@0.6.3
added 1 package in 9.628s
✔  Successfully installed plugin: vue-cli-plugin-vuetify

? Choose a preset: Default (recommended)

🚀  Invoking generator for vue-cli-plugin-vuetify...
📦  Installing additional dependencies...

added 7 packages in 10.502s
⚓  Running completion hooks...

✔  Successfully invoked generator for plugin: vue-cli-plugin-vuetify
   The following files have been updated / added:

     src/assets/logo.svg
     src/plugins/vuetify.js
     package-lock.json
     package.json
     public/index.html
     src/App.vue
     src/components/HelloWorld.vue
     src/main.js

   You should review these changes with git diff and commit them.
```
非常简单，是不是。

![vuetify-installed.png]({{ site.url }}/img/vuetify-installed.png)

接下来是项目要用的几个常用依赖，vuex，axios，vue-analytics，momentjs：

```
➜  super-vue git:(master) ✗ npm install vuex --save
➜  super-vue git:(master) ✗ npm install moment -S
➜  super-vue git:(master) ✗ npm install vue-analytics -S
```

除此之外，vuetify会使用google material design的icon，所以我们要安装相应依赖：

```
➜  super-vue git:(master) ✗ npm install --save material-design-icons-iconfont
```

至此，项目初期需要的依赖都安装完毕，接下来就可以痛快的码代码了。

