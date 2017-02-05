#!/bin/bash

# unregister take_pic.py as service from systemd
#
sudo systemctl stop take_pic.service
sudo systemctl disable take_pic.service 
sudo rm /etc/systemd/system/take_pic.service
#rm /etc/systemd/system/take_pic.service symlinks that might be related
sudo systemctl daemon-reload
sudo systemctl reset-failed
