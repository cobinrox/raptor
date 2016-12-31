set mr=./WEB-INF/lib

set jsonjar="%mr%/json-simple-1.1.jar"

set logjar="%mr%/log4j-1.2.17.jar"

set pi4jjar="%mr%/pi4j-core-0.0.5.jar"

set rapjar="%mr%/io-1.jar"

echo "rapjar is" 
echo %rapjar%

java -cp .;./WEB-INF/classes;%logjar%;%jsonjar%;%pi4jjar%;%rapjar% com.cobinrox.io.DoMotorCmd WEB-INF/classes/io.properties
