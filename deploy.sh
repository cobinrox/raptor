#!/bin/bash
#
# Copy rap-web directory to tomcat deploy directory
# and restart tomcat
#
#
# backup web.xml which contains start-up params including properties file name/location
echo "backing up web.xml..."
sudo cp /var/lib/tomcat8/webapps/rap-web/WEB-INF/web.xml /tmp/web.xml
echo "copying rap-web ..."
sudo cp -r rap-web/target/rap-web /var/lib/tomcat8/webapps
echo "restoring web.xml..."
sudo cp /tmp/web.xml /var/lib/tomcat8/webapps/rap-web/WEB-INF
set -x
echo "restarting tomcat..."
sudo service tomcat8 restart
