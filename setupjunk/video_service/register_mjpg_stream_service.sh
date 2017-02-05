#!/bin/bash

# register mjpg_stream.sh as a systemd service which starts up
# at boot
#
sudo systemctl enable /home/pi/share/raptor/setupjunk/video_service/mjpg_stream.service
sudo systemctl daemon-reload
sudo systemctl start mjpg_stream.service

echo "Thank you for registering, see /var/log/daemon.log for start up info or errors"
