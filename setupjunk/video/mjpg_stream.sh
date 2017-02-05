#!/bin/bash
# starts up the mjpg stream service as an http server
# over port 9090
#
echo "STARTIN MJPG ALREADY MONKEY MONKEY MONKEY"
echo "STARTING MJPG HOO HAH" > /tmp/mjpg.log
export LD_LIBRARY_PATH=/usr/local/lib 
mjpg_streamer -i "/usr/local/lib/input_file.so -f /var/lib/tomcat8/webapps/rap-web -n img.jpg" -o "/usr/local/lib/output_http.so -p 9090 -w /usr/local/www" 
echo "STARTED MJPG" >> /tmp/mjpg.log
