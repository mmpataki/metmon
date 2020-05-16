# metmon [![Build Status](https://travis-ci.com/mmpataki/metmon.svg?branch=master)](https://travis-ci.com/mmpataki/metmon)  
A simple metrics monitor with simplicity and efficiency in mind. 

### Features
- Ships with a hadoop sink which can be used in hadoop components like
    - HBase
    - HDFS
    - Yarn
    - ...
- Simple binary and JSON protocol using which, anyone can publish metrics

### Build steps
1. Get the sources
    ````bash
    $ git clone https://github.com/mmpataki/metmon
    $ cd metmon
    ````
2. Compile
   ````bash
   $ mvn clean install
   ````
3. Package (output zip will be in metmon.assembly/target)
    ````bash
    $ mvn clean package -DskipTests
    ````

### Usage with Hadoop components
0. Start the metmon server somewhere
    ````bash
    $METMON_HOME/bin/start.sh
    ````
1. Get the packaged zip
2. Copy the `$METMON_HOME/sink/metmon-sink*.jar` to the `$CLASSPATH` of the components.
3. Add the following config to the metrics config file (`hadoop-metrics2.properties`)
    ````properties
    hbase.sink.metmon1.class=metmon.hadoop.sink.MetmonSink
    hbase.sink.metmon1.url=
    hbase.sink.metmon1.procGrp=
    hbase.sink.metmon1.procName=
    ````

#### Config properties
````properties
url := to_be_resolved
procGrp := to_be_resolved
procName := to_be_resolved

to_be_resolved := to_be_resolved | to_be_resolved ';' to_be_resolved | with_substring
with_substring := may_be_value | may_be_value '(' [0-9]+ ',' [0-9]+ ')'
may_be_value := constant | from_jvm_arg | from_env_var

constant := metmon_http_url | proc_group_name | process_name
metmon_http_url := "http://xyz.abc.com:8080"  // http url of the metmon server
proc_group_name := [A-Za-z0-9]* //an arbitary name for group of the processes (for grouping)
process_name := [A-Za-z0-9]* //name for the process who is publishing these metrics

from_jvm_arg := '-D' key
from_env_var := '-E' key
key := [A-Za-z0-9]+ //JVM argument / Environment variable name (the value for this key will be read)

Refer below links for implementation and tests:
https://github.com/mmpataki/metmon/blob/master/metmon.sink/src/main/java/metmon/hadoop/sink/MetmonSink.java:resolve
https://github.com/mmpataki/metmon/blob/master/metmon.sink/src/test/java/TestNameResolution.java
````

##### What is resolve()?
To dynamically discover / create process-group and process names, few utilities are provided. Using which one can create values for process-group and process dynamically based on the execution environment. For eg.

One may want to monitor HBase executing on Yarn deployed via apache slider. Since there can be many instances (possibly in different timeframes) one may want to separate the metrics for each instance.

We can achieve this by using the below configuration
````properties
# for master
hbase.sink.metmon1.class=metmon.hadoop.sink.MetmonSink
hbase.sink.metmon1.url=http://foo.bar.com:8080
hbase.sink.metmon1.procGrp=myhbase_application;-ECONTAINER_ID(9,37)
hbase.sink.metmon1.procName=HMaster;-ECONTAINER_ID

# for regionserver
hbase.sink.metmon1.class=metmon.hadoop.sink.MetmonSink
hbase.sink.metmon1.url=http://foo.bar.com:8080
hbase.sink.metmon1.procGrp=myhbase_application;-ECONTAINER_ID(9,37)
hbase.sink.metmon1.procName=HRegionServer;-ECONTAINER_ID
````
