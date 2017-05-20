#!/bin/bash

# register chk_gpio_state.sh as a systemd service which starts up
# at boot
#
sudo systemctl enable /home/pi/share/raptor/setupjunk/chk_gpio_state_service/chk_gpio_state.service
sudo systemctl daemon-reload
sudo systemctl start chk_gpio_state.service

echo "Thank you for registering, see /var/log/daemon.log for start up info or errors"
