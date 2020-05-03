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
url := `resolve(URL of the metmon server)`  
procGrp := `resolve(Process group the app being configured belong to)`   
procName := `resolve(Process name of this process in the group)`

##### What is resolve()?
This is the definition of the resolve().
````java
String resolve(String x) {
    if(x.startsWith("-D")) {
        return System.getProperty(x.substring(2));
    } else if(x.startsWith("-E")) {
        return System.getEnv(x.substring(2));
    }
    return x;
}
````