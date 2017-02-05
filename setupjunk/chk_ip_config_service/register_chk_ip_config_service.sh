#!/bin/bash

# register chk_ip_config.sh as a systemd service which starts up
# at boot
#
sudo systemctl enable /home/pi/share/raptor/setupjunk/chk_ip_conifg_service/chk_ip_config.service
sudo systemctl daemon-reload
sudo systemctl start chk_ip_config.service

echo "Thank you for registering, see /var/log/daemon.log for start up info or errors"
