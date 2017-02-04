#!/bin/bash
#
# Use this to check if your systemd service was started at
# boot and is running.
# If it isn't running, then check /var/log/daemon.log for possible
# info about why it didn't start
sudo systemctl list-unit-files
