#!/bin/bash
#
mr=$HOME
mr+="/.m2/repository"

logjar=$mr
logjar+="/log4j/log4j/1.2.17/log4j-1.2.17.jar"

pi4jjar=$mr
pi4jjar+="/com/pi4j/pi4j-core/0.0.5/pi4j-core-0.0.5.jar"

pi4j="/home/pi/pi4j.jar"

echo $pi4j

java -cp .:./io/target:./io/target/classes:./common/target/classes:$logjar:$pi4jjar com.cobinrox.io.impl.gpio.EzWiringPiGpioPulse
