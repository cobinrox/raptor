#!/bin/bash
#
# Copy rap-web directory to tomcat deploy directory
# and restart tomcat
#
#
sudo cp -r rap-web/target/rap-web /var/lib/tomcat8/webapps/rap-web
set -x
sudo service tomcat8 restart
