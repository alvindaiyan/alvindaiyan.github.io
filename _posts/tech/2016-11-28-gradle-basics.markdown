---
layout: post
title:  "Gradle Basics"
date:   2016-11-27 21:08:00
author:     "Yan"
header-img: "img/gradle-banner.svg"
catalog: true
comments: true
tags:
    - java
---


# Gradle Fundamentals

## Ant

### XML build Script:

- hard to read
- difficult to maintain

### Maven

- many conventions
- highly extendable
- hard to read
- could be difficult to maintain

### Gradle Build

- Gradle has build file
    - Typically build.gradle
- This contains tasks (task based)
    - plugins
    - dependencies
    - mostly tasks


ex:

{% highlight java %}

build.gradle

task hello {
    doLast {
        println “Hello, Gradle”
    }
}
{% endhighlight %}


# Introduction to the Gradle Wrapper

e.g.
{% highlight java %}

task wrapper (type: Wrapper) {
    gradleVersion = ‘2.6’
}
{% endhighlight %}


## Now can run gradlew build

- download the gradle
- then build 

## Writing Simple Tasks

### Task:
- code that grade executes
    - has a lifecycle
    - has properties
    - has ‘actions’ (code to be execute)
    - has dependencies

E.g. (in groovy)

{% highlight java %}

project.task(“Task1”)

{% endhighlight %}

* project is the top level object

{% highlight java %}

task(“Task2”)

task “Task3”

task Task4

Task4.description = “Task 4 Description”
{% endhighlight %}

## Running a Task

— gradle Task4

e.g.

{% highlight java %}

Task4.doLast {
    println “This is task 4”
}

——————————————————————

Task3 << {println “This is task 3”}

——————————————————————

Task task 5 << {println “this is task 5”}

task5 << {println “Another closure“}  // if we save this and when we run gradle build, could replace the first one, or append to the last one

——————————————————————
// some execution phases
task task6 {
    description “this is task 6“
    doLast {
        println “this is task 6”
    }
}

{% endhighlight %}

# Build Phases

1. initialisation phases: used to configure multi projects builds
2. Configuration Phase: executes code in the task that’s not the action e.g. .. description “some thing”…
3. Excution Phase: doLast, doFirst

# Task Dependencies

e.g.

{% highlight java %}

Task6.dependsOn Task5 // when we run task6, we will see Task5 run first.
{% endhighlight %}

# Setting properties on Tasks

e.g.
{% highlight java %}

def projectVersion = “2.0” // local variable, only available to the build.gradle


project.ext.projectVersion = “2.0” // global variable to all the projects

// or 

ext.projectVersion = “2.0”

{% endhighlight %}


# Task Dependencies

- understand how tasks can be linked

## Using mustRunAfter and shouldRunAfter

- mustRunAfter
    - if two tasks execute on must run after the other
- shouldRUnAfter
    - if two tasks execute one should run after the other
    - this  ignores circular dependencies
- finalised by (incubating in 2.6)
    - inverted dependency

# Introduction to Typed Tasks

- suppose we want something more complex
    - can be reuse the task code
- e.g. copying files
    - Open file/ read file/ write file
- e.g. Zipping files
- would like this to be reused

# Using the Copy task

copying some images:

e.g.

{% highlight java %}

def contentSpec = cpySpec {
    exclude ‘IMG_—52.jpg’
    from ‘src’
}

task copyImages2 (type: Copy) {
    // with contentSpec
    exclude { it.file.name.startWith(‘IMG’) } // write groovy code to configure the task
    into  ‘dest’
}

task copyImages (type: Copy) {
    exclude ‘IMG_0052.jpg’
    from ‘src’
    into  ‘dest’
}

task copyConfig (type: Copy) {
    include ‘web.xml’
    from ‘src’
    into  ‘config’
expand {
    [resourceRefName: ‘hsbc 1.0’]
}
}

{% endhighlight %}


# integral to Java Plugin

Defines:
- a source set
- task to compile the test
- task to run the test

Source Set

- Looks for unit tests in src/test/java
- Outputs to build/classes/test
- Reports to build/reports/test

Filtering

- Can filter to only run a subset of tests
    - Single test 
    - All tests from a package
    - Wildcard is supported
e.g.
{% highlight java %}

test {
    filter {
        includeTestsMatching “com.foo.shouldCreateASession”
        includeTestsMatching “*shouldCreateASession”
    }
}

{% endhighlight %}


Other Testing

- Integration tests for example
- add the cradle-testsets-plugin
    - add a testSet
    - set other configurations parameters
    - set output reports directory


buildscript:
- add dependencies for build script not for tasks

allprojects:
- apply all the content to all the projects when built

Wrapper

- provides a specific version of Gradle to the project
    - get consistent builds
    - gradlew.bat on Windows
    - cradle shell script on *unix
- Standard task
    - always available

e.g.
{% highlight java %}

task wrapper(type: Wrapper) {
    gradleVersion = ‘2.6’
}
{% endhighlight %}

## Running the Wrapper

- run the specific gradle
- if not installed will download and install specific gradle

