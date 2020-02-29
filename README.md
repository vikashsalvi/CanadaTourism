# CSCI5409_A2
A marathon cluster program, marathon unning on top of mesos cluster.

a.	Install Mesos using below command:

sudo brew install mesos   

b.	After installation of Apache Mesos, install Apache zookeeper by downloading its source from its official website. Once installed and configured zookeeper, started it using the following command:
     
     sudo ./bin/zkServer.sh start

c.	After starting zookeeper we have to start mesos-master and mesos-slave, it can be done using following command:

     /usr/local/sbin/mesos-master  --zk=zk://127.0.0.1:2181/mesos --quorum=1 --registry=in_memory  --work_dir=/tmp/messo

     /usr/local/sbin/mesos-slave --master=zk://127.0.0.1:2181/mesos --work_dir=/tmp/mesos


Download and install marathon 

a.	Download Marathon using:
     curl -O http://downloads.mesosphere.com/marathon/v1.3.1/marathon-1.3.6.tgz

b.	Untar it using: 
     tar xzf marathon-1.3.1.tgz

c.	Go into the marathon folder using:
     cd marathon-1.3.1

d.	Start Apache zookeeper, and Apache Mesos and to start marathon run this command:
     ./bin/start –master 127.0.0.1:5050 –-zk zk://localhost:2181/marathon --http_port=7070


Open marathon on localhost:7070 and create 3 application 
1. Fibonacci application 
2. Factorial application
3. Clien application for executng Fibonacci and Factorial app
