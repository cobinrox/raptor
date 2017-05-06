#!/bin/bash
#
mr=$HOME
mr+="/.m2/repository"
jsonjar=$mr
jsonjar+="/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar"

logjar=$mr
logjar+="/log4j/log4j/1.2.17/log4j-1.2.17.jar"

pi4jjar=$mr
pi4jjar+="/com/pi4j/pi4j-core/0.0.5/pi4j-core-0.0.5.jar"

pi4j="/home/pi/pi4j.jar"

libusb4j="/home/share/raptor/mylib/org/usb4java/libusb4java/1.2.1-SNAPSHOT/libusb4java-1.2.1-SNAPSHOT.linux-arm.jar"

usb4j="/home/share/raptor/mylib/org/usb4java/1.2.1-SNAPSHOT/usb4java-1.2.1-2016-1108.093308-324.jar"

#echo $pi4j

sudo java -cp .:./io/target:./io/target/classes:./common/target/classes:$libusb4j:$usb4j:$logjar:$jsonjar:$pi4jjar:./io/target/rap_io.jar com.cobinrox.io.DoMotorCmd sparkwc.properties
