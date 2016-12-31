#!/bin/bash
#
mr=./WEB-INF/lib
jsonjar=$mr
jsonjar+="/json-simple-1.1.jar"

logjar=$mr
logjar+="/log4j-1.2.17.jar"

pi4jjar=$mr
pi4jjar+="/pi4j-core-0.0.5.jar"

rapjar=$mr
rapjar+="/io-1.jar"

echo "rapjar is" 
echo $rapjar

java -cp .:./WEB-INF/classes:$logjar:$jsonjar:$pi4jjar:$rapjar com.cobinrox.io.DoMotorCmd WEB-INF/classes/io.propertie
