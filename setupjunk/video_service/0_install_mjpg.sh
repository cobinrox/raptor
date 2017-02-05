#!/bin/bash

sudo apt-get install subversion

sudo apt-get -y install libjpeg8-dev imagemagick libv4l-dev

sudo ln -s /usr/include/linux/videodev2.h /usr/include/linux/videodev.h

mkdir tmpinstall 

svn co https://svn.code.sf.net/p/mjpg-streamer/code/mjpg-streamer/ tmpinstall

#wget https://sourceforge.net/code-snapshots/svn/m/mj/mjpg-streamer/code/mjpg-streamer-code-182.zip70
#unzip mjpg-streamer-code-182.zip
#cd mjpg-streamer-code-182/mjpg-streamer

ch tmpinstall
make mjpg_streamer input_file.so output_http.so

sudo cp mjpg_streamer /usr/local/bin

sudo cp output_http.so input_file.so /usr/local/lib/

sudo cp -R www /usr/local/www

mkdir /tmp/stream

echo "mjpg_streamer installed, please run register script to enable start up at boot"

