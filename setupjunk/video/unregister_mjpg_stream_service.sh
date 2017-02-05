#!/bin/bash

#
# remove mjpg_stream.sh service from systemd
#
sudo systemctl stop mjpg_stream.service
sudo systemctl disable mjpg_stream.service 
sudo rm /etc/systemd/system/mjpg_stream.service
sudo systemctl daemon-reload
sudo systemctl reset-failed
