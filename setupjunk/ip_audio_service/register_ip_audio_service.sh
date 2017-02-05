#!/bin/bash

# register ip_audio.sh as a systemd service which starts up
# at boot
#
sudo systemctl enable /home/pi/share/raptor/setupjunk/ip_audio_service/ip_audio.service
sudo systemctl daemon-reload
sudo systemctl start ip_audio.service

echo "Thank you for registering, see /var/log/daemon.log for start up info or errors"
