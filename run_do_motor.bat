@REM
@REM Run the DoMotor command which allows you to send signals out to the GPIO pins
@REM attached to a motor
@REM Note that this is for a windows run command, so you should set the
@REM field "SIMULATE_PI" in the io.properties file to "true" to simulate
@REM physical i/o attempts
@REM

@REM define location of maven repository, based on the Windows
@REM USERPROFILE environment variable, where for W7, USERPROFILE
@REM will usually resolve to C:/Users/your.os.name.here.
@REM
@REM If the Windows user env variable USERPROFILE is not defined
@REM in your version of windows, then
@REM replace the USERPROFILE entry below with C:/Users/your.os.name.here
@REM
set mr=%USERPROFILE%/.m2/repository

@REM
@REM JSON jar
@REM
set json=%mr%/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar

@REM
@REM log4j jar
@REM
set logjar=%mr%/log4j/log4j/1.2.17/log4j-1.2.17.jar

@REM
@REM pi4j jar
@REM
set pi4j=%mr%/com/pi4j/pi4j-core/0.0.5/pi4j-core-0.0.5.jar

@REM java command, assumes java available in path
java -cp .;%logjar%;%json%;%pi4j%;./common/target/classes;./io/target/classes com.cobinrox.io.DoMotorCmd io/target/io.properties