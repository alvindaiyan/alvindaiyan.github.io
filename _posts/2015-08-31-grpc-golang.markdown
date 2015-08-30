---
layout: post
title:  "General Learnings about gRpc-golang"
date:   2015-08-31 10:00:00
categories: go
---

[gRpc](http://www.grpc.io/) is a remote procedure call framework developed by Google. RPC is a alternative way to develop a web service compared with RESTful architecture. While, comparing gRpc with RESTful is not proper. The reason is gRpc is a framework with protobuf3 but RESTful is really just an architecture design of a web application. Therefore, you can find lots of article by googling "restful vs rpc" but not "restful vs grpc". In this post, I won't do more comparison.

## gRpc

### proto

- service is an entry point for the software. Each service contains lots of different methods, some people may call it micro services. 

- a micro service start with rpc key words, followed by the service name. Then a bracket with the parameters name is included. The following key words is returns and then a bracket with the returned type.

- stream is modifier to the param type or the return type. A stream can be used to keep the communication between a server and a client alive for a while. This is similar to the Java InputStream and OutputStream. (Stream is great!!!)

- a message like a struct in Golang which defines a structure of a message and used for protobuf. Then protobuf uses its algorithm to serialise and deserialise the message.


### gRpc installation

- need the latest protoc3. Currently, protoc3 is in alpha release.

- get the packages needed for compile gRpc proto. e.g. google.golang.org/grpc, github.com/golang/protobuf/proto-gen-go and so on.

- make the proto-gen-go project. This is a plugin program for go support used by protoc. Then the compiled file will be located in the bin folder. Using the compiled program to generate the go program from .proto file with protoc.

### gRpc go

- the auto generated file is with extension .pb.go

- to import the .pb.go to use using the import clause ```import "github.com/path/to/proto/"```

- there are two program interfaces for go in .pb.go. First one is the Server API and the other one is the Client API. Using these two APIs to perform the remote procedure call.

### gRpc general

- gRpc uses http2 which is a new version of http. The performance is better than http1.1.
    
- gRpc is a great way to make distributed application with different programming lanugages.

- gRpc has a software support to node.js.

- another comparison should be with 0mq and rabbitmq.

### gRpc compilation

- open terminal and run ```protoc --go_out=plugins=grpc:. route_guide.proto```

## golang 

### channel

- a wait channel is to use to wait for a thread to finish. Most of times the program exit before all the threads finished then, it will terminate all the thread. By using a wait channel, the main thread will wait untill the channel closed. It dosen't matter for whether there are things in the channel or not when dequeue from the channel. If there is an empty channel for dequeue, an empty message will be returnted.

## git

- LF will be replaced by CRLF: there is no harmful thing for this message, as the EOF of Windows and Unix is different.

## algorithm

- little Endian and big Endian:


## front-end
 
- yeoman has better support for Windows than Linux.

