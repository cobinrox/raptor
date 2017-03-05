#!/bin/bash
#
# Copy rap-web directory to tomcat deploy directory
# and restart tomcat
#
#
echo "backing up io.properties..."
sudo cp /var/lib/tomcat8/webapps/rap-web/WEB-INF/classes/io.properties /tmp/io.properties
echo "copying rap-web ..."
sudo cp -r rap-web/target/rap-web /var/lib/tomcat8/webapps/rap-web
echo "restoring io.properties..."
sudo cp /tmp/io.properties /var/lib/tomcat8/webapps/rap-web/WEB-INF/classes/
set -x
echo "restarting tomcat..."
sudo service tomcat8 restart
