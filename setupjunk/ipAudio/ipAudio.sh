#!/bin/bash

#
# Script to send IP of wireless adapter to audio port.
# Run in a loop to repeat 10 times
# Requires festival library.
#
echo "ipAudio is running"

for i in {1..10}
do
	echo "ipAudio running number $i"
	/usr/bin/amixer cset numid=3 1
	/sbin/ifconfig | /bin/grep -A3 wlan0 | /bin/grep inet | \
	/usr/bin/tr ':' ' ' | /usr/bin/cut -d\  -f 13 | \
	/usr/bin/festival --tts
done
echo "ipAudio says donesky"

