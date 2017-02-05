#!/bin/bash

#
# remove ip_audio.sh service from systemd
#
sudo systemctl stop ip_audio.service
sudo systemctl disable ip_audio.service
sudo rm /etc/systemd/system/ip_audio.service
sudo systemctl daemon-reload
sudo systemctl reset-failed
