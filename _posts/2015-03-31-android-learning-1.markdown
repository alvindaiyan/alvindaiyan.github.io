---
layout: post
title:  "Android Learning Note (1)"
date:   2015-03-31 10:00:00
categories: android
---

This post is to write down what I've learnd by using Android. I am a Java developer, and trying to learn Android for fun and future career. There are two reasons why I would like to learn Android. Firstly, I am naturally interested on any mobile apps. I like developing small games and tools for my mobiles. It is very fun to play and run your code on your own mobile device. And secondly Android is one of the most popular mobile OS in world. As a developer, Android uses Java as the programming language which makes it really cheap to learn. 

![android notes]({{ site.url }}/assets/android_notes.png)

## Four Fundamental Building Blocks

1. Activity Class: 
    - To provide GUI to user, 
    - The others do not have UI
    - Primary Class for user interaction
    - For single focused tasks

2. Service
    - Run in background
    - To perform long-running operations
    - Support remote interaction

3. BroadcastReceiver
    - Component listen for and responds to events
    - Use publish/subscribe pattern
    - Events represented by the intent class and broadcast
    - Broadcastreceiver receive event and respond

4. ContentProvider
    - share data
    - Store and share Data across applications
    - Uses database-style interface
    - Handles interprocess communication

## Building an Application

The figure below shows an particular process to build an android project. Also, using [Gradle](http://developer.android.com/sdk/installing/studio-build.html) is a very good idea to build and manage android project. Android project building process:

![android-build process]({{ site.url }}/assets/android_build_process.png)


## Activity Class

1. The activity class
  - Applications has multiple activities
  - Android supports navigation in serveral ways

2. Tasks
  - A task is a set of related activities
  - Don't have to be in same applications
  - Most task start at the home screen
  - The task backstack
  - Suspending & resuming activities

3. Activity lifecycle - (Just Override ~)
  - resumed/running - visible, user interacting
  - paused - visible, user not interacting, can be terminated
  - stopped - not visible, can be terminated
  - I know this diagram is too classic, but I still like to share:
  ![android activity life cycle]({{ site.url }}/assets/AndroidActivityLIfecycle.png)  
4. Understanding each part on the activity lifecycle

  - onCreate() : 
    1. call super.onCreate()
    2. set the activity's content view
    3. retain reference to UI views as necessary
    4. configure views as necessary

  - onRestart():
    1. call if the activity has been stopped and is about to be started again
    2. typical actions : special processing needed only after having been stopped

  - onStart()
    1. activity is about to become visible
    2. typical actions: 
      - start when visible - only behaviors
      - loading persistent application state

  - onResume()
    1. activity is about to interacting with users
    2. start foreground actions like animations

  - onPause()
    1. focus about to switch to another activity
    2. shutdown foreground: killing animation or persistent state

  - onStop()
    1. activity is no longer visible to user
    2. may not be called if android kills your app

  - onDestroy()
    1. activity is about to be destroyed

### Starting activities
  - pass newly created intent to methods such as startActivity(), startActivityforResult()
  - invokes a callback method when the called activity finishes to return a result. To start activity:
  {% highlight java %}  
    Intent intent = new Intent(Intent.ACTION_PICK, CONTACTS_CONTENT_URI);
startActivityForResult(intent, PICK_CONTACT_REQUEST);
  {% endhighlight %}

Return a result from an activity by call ```Activity.setResult()```, ResultCode(an int): ```RESULT_CANCELED```,```REULT_OK```, ```RESULT_FIRST_USER```, custom result codes can be added:
  {% highlight java %}  
@Override
void onActivityResult(int requestcode, int resultCode, Intent data){}
  {% endhighlight %}
### Handling configuration changes
  - device configuration can change at runtime (keyboard, orientation, locale)
  - on configuration changes, android usually kills the current activity & then restart it
  - by override onRetainNonConfigurationInstance() to build & return configuration object 
    - will be called between onStop() and onDestroy()
  - retaining an object by call getLastNonConfigurationInstance() during onCreate() to recover retained object (could be deprecated on fragment class)
  - by declare manually in ```androidmanifest.xml```

## The Intent Class
1. Starting activities with Intent
  - A data structure that represent to be performed or
  - An event that has occurred
  - adb shell dumpsys package > data.txt
2. explicit activation
  - can be named explicitly by setting the intent’s components
3. implicit activation via intent resolution
- can be determined implicitly, when activity to be activated is not explicitly named, android tries to find activities that match intent
- Receiving Implicit intent
  -to receive implicit intents an activity should specify an intentfilter with the category
- Priority:
  - android:priority - priority given to the parent component when handling matching intents
  - causes andorid to prefer one activity over another
  - values shoud be greater than -1000 to 100, higher value in higher priority 
- This process is called intent resolution:
  - rely on an intent describing a desired operation
  - IntentFilters which describe which operations an activity can handle
    - specified either in ANDROIDMANIFEST.xml or programmatically 
  - Intent Resolution data
    - Action, 
    - Data (both uri & type)
    - category

#### Specifying IntentFilters:
  {% highlight xml %}
  <activity ...>
          <intent-filter ...>
          …
            <action android:name=”actionName”/> 
            e.g. <action android:name =”android.intent.action.DIAL”/>           
          …
          </intent-filter>
        …
  </activity>
  {% endhighlight %}
#### Adding Data to IntentFilter:
{% highlight xml %}
<intent-filter ...>
    ...
    <data
      android:mimeType=”string”
      android:scheme=”string” e.g.”geo”
      android:host=”string”
      android:port=”string”
      android:path=”string”
      android:pathPattern=”string”  
      android:pathPrefix=”string”
    />
    <category ...>
</intent-filter>
{% endhighlight %}

### Intents as desired operations
1. Intents provide a flexible language for specifying operations to be performed
  - e.g. pick a contact or take a photo
2. intent is constructed by one component that wants some work done
3. received by one activity that can perform that work

### Intent fields
1. action
  - string representing desired operation, e.g. ACTION_DIAL - dial a number
  - setting the intent action:
    - ```Intent newint = new Intent(Intent.ACTION_DIAL);```
    - ```Intent intent = new Intent(); intent.set();```
2. data
  - data associated with the intent: 
    - formatted as a uniform resource identifier (URI). e.g. uri.parse(“...”)
    - e.g. Data to view on map
    - e.g. number to dial in the phone app
    - setData() or in constructor
3. category
  - additional information about the components that can handle the intent
4. type
  - specifies the mime type of the intent data
  - if not set, android will match the type itself
  - set by setType(), or setDataAndType
5. component
  - the component that should receive this intent
  - use this when there’s exactly one component that should receive the intent
  - ```Inent(Context packageContext, Class<?> cls)``` or ```setComponent()```, ```setClass()``` or ```setClassName()```
  - Context is an interface used to access global application information
6. extras
  - additional information associated with intent treated as a map (key-value pairs)
  - e.g. Intent.EXTRA_EMAIL: email recipients
  - ```putExtra(String name, float[] floats)``` or ```putExtra(String name, String string)```
7. flags
  - specify how the intent to be handled
  - e.g. ```FLAG_ACTITY_NOHISTORY```, ```FLAG_DEBUG_LOG_RESOLUTION```


## Permissions

1. android permissions 
  - android protects resource and data with permissions
  - used to limit access to 
    - user information - e.g. contacts
    - cost-sensitive APIs- e.g. sms/mms
    - system resource -  e.g. camara
  - permission are represented as strings
  - in androidmanifest.xml, apps declare the permissions
  - they use the permissions themselves
  - they require of other components to use the permission to invoke themselves
2. defining & using application permissions
  - defining permissions:
    - suppose your application a privileged/dangerous operations
    - use the tag ```<permission ...></permission>``` to declare a permission
    - should be include this permission with the application
  - app specify permissions use through a <uses-permission> tag
  - users must accept these permissions before an application can be installed: 
  {% highlight xml %}

  <manifest ...>
    <uses-permission android:name=”android.permission.CAMERA” />
    <uses-permission android:name=”android.permission.INTERNET” />
    <uses-permission android:name=”android.permission.ACCESS_FINE_LOCATION” />
</manifest>
  {% endhighlight %}

### Component permissions & permissions-related APIs
  - individual components can set their own permissions, restricting which other components can access them
  - component permissions take precedence over application-level permissions
    - activity permissions : rastrigin which components can start the associated activity, checked with startActivity(), startActityForResult()
    - service permissions: checked within context.startServices(), context.stopService(), context.bindService()
    - BroadcastReceviver permissions
    - ContentProvider permissions

