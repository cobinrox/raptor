#!/bin/bash

#
# remove chk_ip_config.sh service from systemd
#
sudo systemctl stop chk_ip_config.service
sudo systemctl disable chk_ip_config.service
sudo rm /etc/systemd/system/chk_ip_config.service
sudo systemctl daemon-reload
sudo systemctl reset-failed
