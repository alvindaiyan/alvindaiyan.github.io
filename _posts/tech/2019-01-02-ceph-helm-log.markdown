---
layout: post
title:  "Installing Ceph on K8s by Helm"
date:   2019-01-02 21:00:00
author:     "Yan"
header-img: "img/the_dark_times_are_coming_by_msdudettes-d5m99ns.jpg"
catalog: true
comments: true
tags:
    - devops
---

This blog is written at Jan 2019. And the k8s and ceph-helm version used are:

- k8s: v1.12.2
- ceph chart: 743a7441ba4361866a6e017b4f8fa5c15e34e640, which is the head of the master branch of [ceph-helm](https://github.com/ceph/ceph-helm).

# What is ceph

In a simple term:

- Ceph is a opensource software storage platform. The core members of ceph have included Canonical, Cisco, Red Hat, Intel e.g.

- Ceph implements object storage and provides object-, block- and file-level storage

# Why helm

Helm is a package management tool for kubernetes. There are basically two parts for Helm, the helm client and tiller.
According to its [documentation](https://docs.helm.sh/using_helm/), Helm is very easy to install to a k8s cluster:

{% highlight bash %}

    #!/bin/bash

    # install helm client
    brew install kubernetes-helm

    # initial Helm
    helm init

    # if you need change the helm repo or for some reason you cannot the offical
    # resource of helm which is hosted by google cloud
    helm init --upgrade -i \
    registry.cn-hangzhou.aliyuncs.com/google_containers/tiller:v2.12.0 \
    --stable-repo-url https://kubernetes.oss-cn-hangzhou.aliyuncs.com/charts \
    --node-selectors "tiller-enabled"="true"

    # upgrade tiller
    helm init --upgrade

    # delete or reinstall tiller
    # besides, most of time, you will don't need delete the helm chart already
    # deployed in the k8s by helm.
    # Those helm-charts will be manageable after the tiller is re-installed
    kubectl delete deployment tiller-deploy --namespace kube-system

    # or run
    helm reset

    # then reinstall tiller.
    helm init
{% endhighlight %}

# Install Ceph by Helm

## 1. Build the ceph-chart

Checking out and building the ceph helm chart is the very first thing the offical tutorial introduced. By reading this [issue](https://github.com/ceph/ceph-helm/issues/64) on the offical ceph helm chart repo, the ceph-helm in official helm repo is very outdated.

{% highlight bash %}

    #!/bin/bash

    # initial a local helm repo
    helm serve &
    helm repo add local http://localhost:8879/charts

    # checkout the ceph helm chart repo, and build
    git clone https://github.com/ceph/ceph-helm
    cd ceph-helm/ceph
    make

{% endhighlight %}

Once above done, the helm will use the local helm chart to install ceph into your k8s.

## 2. Install Ceph Cluster into K8s

Below is a typical ceph config file for k8s:

{% highlight bash %}

    #!/bin/bash

    cat ~/ceph-overrides.yaml
    # network:
    #   public:   10.244.0.0/16
    #   cluster:   10.244.0.0/16

    # osd_devices:
    #   - name: dev-sdb
    #     device: /dev/sdb
    #     zap: "1"
    # storageclass:
    #   name: ceph-rbd
    #   pool: rbd
    #   user_id: k8s

    # create namespace for ceph
    kubectl create namespace ceph

    # configure the ceph rbac in k8s by the rbac.yaml in the ceph chart repo
    kubectl create -f ~/ceph-helm/ceph/rbac.yaml

    # label the k8s node for deploying the ceph monitor and ceph manager
    kubectl label node <nodename> ceph-mon=enabled ceph-mgr=enabled

    # label the k8s node for deploying the ceph osd agent and the actual machine
    # contains the hard drive
    kubectl label node <nodename> ceph-osd=enabled ceph-osd-device-dev-sdb=enabled

    # to remove the ceph osd for some reason,
    # you just need delete the ceph-osd-device-dev-sdb=enabled label
    kubectl label node <nodename> ceph-osd-device-dev-sdb-

    # now, deploy ceph cluster by helm
    helm install --name=ceph local/ceph --namespace=ceph -f ceph-overrides.yaml

{% endhighlight %}

Make sure the public and cluster IP are same and covered by the actual host's IP range. Otherwise, the ceph network will not function well.

The `osd_devices` part is used to define the physical hard drive that will be used as the storage device. You must make sure that the hard drive is clean and not contain any partition. Otherwise, you can use the following command to checkout and delete those partitions:

{% highlight bash %}

    #!/bin/bash

    # check/dev/sdb the status of sbd
    lsblk /dev/sdb
    # NAME   MAJ:MIN RM SIZE RO TYPE MOUNTPOINT
    # vdb    252:16   0  40G  0 disk
    # |-vdb1 252:17   0  35G  0 part
    # `-vdb2 252:18   0   5G  0 part

    # remove the header info by clean the very first 110 mb
    dd if=/dev/zero of=/dev/sdb1 bs=1M count=110 oflag=sync
    dd if=/dev/zero of=/dev/sdb2 bs=1M count=110 oflag=sync

    # delete the partition by parted
    parted /dev/sdb rm 1
    parted /dev/sdb rm 2

{% endhighlight %}

You will need run this every time you reinstall ceph cluster and want ceph to use that hard drive as its storage again.

After you've created the ceph chart, you might have an unhealthy cluster instead of a healthy one in the offical tutorial. Don't worry about this, if it's just unhealthy caused by unactive pgs. for example my one:

![ceph-unhealth.png]({{ site.url }}/img/ceph-unhealth.png)

## 3. Configure the keys for a pod to use PVC

Now we need configure the keys for a pod in a namespace so that it can use the persistent volume claim.

{% highlight bash %}

    #!/bin/bash

    # go to the ceph-mon pod
    kubectl -n ceph exec -ti ceph-mon-cppdk -c ceph-mon -- bash
    # and get the auth key
    ceph auth get-or-create-key client.k8s mon 'allow r' osd \
    'allow rwx pool=rbd'  | base64
    # QVFCLzdPaFoxeUxCRVJBQUVEVGdHcE9YU3BYMVBSdURHUEU0T0E9PQo=
    exit

    # edit the pod to use the key
    kubectl -n ceph edit secrets/pvc-ceph-client-key

    # apiVersion: v1
    # data:
    # key: QVFCLzdPaFoxeUxCRVJBQUVEVGdHcE9YU3BYMVBSdURHUEU0T0E9PQo=
    # kind: Secret
    # metadata:
    # creationTimestamp: 2017-10-19T17:34:04Z
    # name: pvc-ceph-client-key
    # namespace: ceph
    # resourceVersion: "8665522"
    # selfLink: /api/v1/namespaces/ceph/secrets/pvc-ceph-client-key
    # uid: b4085944-b4f3-11e7-add7-002590347682
    # type: kubernetes.io/rbd

    # Create a Pod that consumes a RBD in the default namespace.
    # Copy the user secret from the ceph namespace to default
    kubectl -n ceph get secrets/pvc-ceph-client-key -o json | \
    jq '.metadata.namespace = "default"' | kubectl create -f -
    # secret "pvc-ceph-client-key" created

    # checkout all hte secrets in default namespace
    kubectl get secrets
    # NAME                  TYPE                                  DATA      AGE
    # default-token-r43wl   kubernetes.io/service-account-token   3         61d
    # pvc-ceph-client-key   kubernetes.io/rbd                     1         20s

{% endhighlight %}

## 4. Adjust the Ceph Pool size

It's almost impossible to have a healthy ceph cluster after first installation. Now, let's fix this. First, create a rbd pool:

{% highlight bash %}

    # create a rbd pool
    kubectl -n ceph exec -ti ceph-mon-cppdk -c ceph-mon -- ceph osd pool create rbd 256
    # pool 'rbd' created

    # initial the rbd pool
    kubectl -n ceph exec -ti ceph-mon-cppdk -c ceph-mon -- rbd pool init rbd

{% endhighlight %}

From what I've noticed, the ceph status depends on the osd size and pool size. My ceph cluster has configured two osd at the very beginning but relatively large pool size. And I've tried several times, none of those tries have successful ceph cluster. The main reason is the default pool size isn't proper for a small osd cluster. To adjust the pool size:

{% highlight bash %}

    #!/bin/bash

    # see all the pool's name and it's size
    ceph osd lspools

    # checkout the pool name and its  size
    ceph osd dump | grep 'replicated size'

    # use ceph osd pool set adjust the pool size
    # ceph osd pool set {poolname} size {num-replicas}
    ceph osd pool set .rgw.root size 1
    ceph osd pool set cephfs_data size 1
    ceph osd pool set cephfs_metadata size 1
    ceph osd pool set default.rgw.control size 1
    ceph osd pool set default.rgw.meta size 1
    ceph osd pool set default.rgw.log size 1

{% endhighlight %}

For my cluster, I simply reduce all the pool size to 1 since I only have 2 osd devices. Afterwards, the ceph cluster status should turn to health.

{% highlight bash %}

    #!/bin/bash

    # check ceph status
    kubectl -n ceph exec -ti ceph-mon-xm7df -c ceph-mon -- ceph -s
    # cluster:
    #     id:     9ab761d6-60bd-4197-9513-2dfe47b3cf57
    #     health: HEALTH_OK

    # services:
    #     mon: 1 daemons, quorum k8s-node1
    #     mgr: k8s-node1(active)
    #     mds: cephfs-1/1/1 up  {0=mds-ceph-mds-68c79b5cc-6r54r=up:active}
    #     osd: 2 osds: 2 up, 2 in
    #     rgw: 1 daemon active

    # data:
    #     pools:   7 pools, 198 pgs
    #     objects: 299 objects, 136 MB
    #     usage:   350 MB used, 11163 GB / 11164 GB avail
    #     pgs:     198 active+clean

{% endhighlight %}

## 5. Install Ceph client on host machine

Installing ceph client into the host machine is missing in all the tutorials about installing ceph by Helm. This is because rbd will be used when Kubelet creates and mounts the pvc to the pod. If you you didn't install ceph client, you may see the following errors or similar and the pod will be stucked at Container Creating stage:

```
rbd: Error creating rbd image: executable file not found in $PATH
```

There is no extra configuration needed to be done after installing rbd or ceph-client.

Install `ceph-common` package on to your host machine which has the ceph-mon and ceph-mgr running is all you need.

{% highlight bash %}
#!/bin/bash

yum install python-rbd
yum install ceph-common
{% endhighlight %}


# 6. Test the Ceph cluster

In order to tell if the ceph cluster is working or not, we can create a pvc and mount it to a pod:

{% highlight bash %}

    #!/bin/bash

    # the pvc configuration file
    cat pvc-rbd.yaml
    # kind: PersistentVolumeClaim
    # apiVersion: v1
    # metadata:
    # name: ceph-pvc
    # spec:
    # accessModes:
    # - ReadWriteOnce
    # resources:
    #     requests:
    #     storage: 20Gi
    # storageClassName: ceph-rbd

    # create the pvc
    kubectl create -f pvc-rbd.yaml
    # persistentvolumeclaim "ceph-pvc" created

{% endhighlight %}

Now, the pvc and its pv should be created. Use `kubectl get pvc` and `kubectl get pv` to see the the pvc and the pv.

Next, we want to create a pod use that pvc we just created:

{% highlight bash %}

    #!/bin/bash

    # create a k8s config file
    cat pod-with-rbd.yaml
    # kind: Pod
    # apiVersion: v1
    # metadata:
    # name: mypod
    # spec:
    # containers:
    #     - name: busybox
    #     image: busybox
    #     command:
    #         - sleep
    #         - "3600"
    #     volumeMounts:
    #     - mountPath: "/mnt/rbd"
    #         name: vol1
    # volumes:
    #     - name: vol1
    #     persistentVolumeClaim:
    #         claimName: ceph-pvc

    # create the pod
    kubectl create -f pod-with-rbd.yaml
    # pod "mypod" created

{% endhighlight %}

# 7. Managing Kubernetes and its network

By the time you create the pod, you may see the container is stucked at conatiner creating stage. And the log could say something like:

```
can not resolve ceph-mon.ceph.svc.cluster.local
```

Remember we've installed the ceph onto the host machine and the rbd on host will be invoked by kubelet during pod creation. The host machine is trying to reach the ceph monitor node and failed since it's actually in the k8s cluster to use the kube-dns. There are few ways to fix this. One is add the kube-dns svc endpoint to `/etc/resolv.conf` as a dns service. Or add the domain mapping to the `/etc/hosts` to my node's host machine.

```
11.7.100.123 ceph-mon.ceph.svc.cluster.local
```
However, the second way is not secure as if the ceph-mon is restart or the IP is changed inside k8s cluster. The ceph cluster will be broke.

After fixing the dns issue, the `cannot resolve ceph-mon.ceph.svc.cluster.local` should be gone. But I see another one which is:

```
timeout expired waiting for volumes to attach/mount for pod :
Error syncing pod 096ac42b-919a-11e8-bd1d-fa163eaae838
("mypod_ceph(096ac42b-919a-11e8-bd1d-fa163eaae838)"), skipping:
timeout expired waiting for volumes to attach/mount for pod
"ceph"/"mypod". list of unattached/unmounted volumes=[vol1]
```

The offical tutorial has documented the reason, which is:

```
Kubernetes uses the RBD kernel module to map RBDs to hosts.
 Luminous requires CRUSH_TUNABLES 5 (Jewel). The minimal kernel
 version for these tunables is 4.5. If your kernel does not
 support these tunables, run ceph osd crush tunables hammer
```

So bash into your ceph-mon pod and run `ceph osd crush tunables hammer`. It's caused by the ceph helm chart is using a old version of luminous image.

# Conclusion

Above is all you need to do for using helm install ceph.

# Delete Ceph cluster

Delete ceph with helm will open another worm can. When you run `helm delete --purge ceph`, two extra one-time pod will be started to delete all keys that ceph created. However, if you run `find / -name "*ceph*"`, you will see there are some leftover that the helm didn't managed well:

```
...
/var/lib/ceph-helm
/var/lib/ceph-helm/ceph
/var/lib/ceph-helm/ceph/mon/bootstrap-osd/ceph.keyring
/var/lib/ceph-helm/ceph/mon/bootstrap-mds/ceph.keyring
/var/lib/ceph-helm/ceph/mon/bootstrap-rgw/ceph.keyring
...

```

Those keys will be a blocker for your brand new ceph cluster trying to communicate to each other. So we have delete them manually everytime if we want to re-install ceph by helm.

Also, you may need remove the partition listed in step 2 when a harddrive is reused.

# Reference

- [Ceph at Wikipedia](https://en.wikipedia.org/wiki/Ceph_(software))
- [Installation (Kubernetes + Helm)](http://docs.ceph.com/docs/mimic/start/kube-helm/)
- [Installing Helm](https://github.com/helm/helm/blob/master/docs/install.md)
- [跟我学 K8S--运维: helm 安装 ceph 到 kubernetes 集群](https://segmentfault.com/a/1190000015806843)
- [ceph-helm](https://github.com/ceph/ceph-helm)
