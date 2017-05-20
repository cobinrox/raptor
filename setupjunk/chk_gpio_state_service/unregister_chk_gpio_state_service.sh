#!/bin/bash

#
# remove chk_gpio_state.sh service from systemd
#
sudo systemctl stop chk_gpio_state.service
sudo systemctl disable chk_gpio_state.service
sudo rm /etc/systemd/system/chk_gpio_state.service
sudo systemctl daemon-reload
sudo systemctl reset-failed
