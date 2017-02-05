#!/bin/bash

#
# register take_pic.py as systemd service to start up at boot
#
sudo systemctl enable /home/pi/share/raptor/setupjunk/video_service/take_pic.service
sudo systemctl daemon-reload
sudo systemctl start take_pic.service

echo "Thank you for registering, see /var/log/daemon.log for errors in starting"
