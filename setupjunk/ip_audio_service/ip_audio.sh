#!/bin/bash

#
# Script to send IP of wireless adapter to audio port.
# Run in a loop to repeat 20 times
# Requires festival library, which you can install via:
# sudo apt-get update
# sudo apt-get install festival
#
echo "ipAudio is running"

for i in {1..20}
do
    ip=`ifconfig wlan0 | grep "inet " | awk -F'[: ]+' '{print $4}'` 
    echo ${ip}
    echo "${ip}" | festival --tts
	echo "ipAudio running loop number $i"
done
echo "ipAudio says donesky"

