# take picture using pi camera video stream and python library
# much faster snapshots can be taken using this script
# place the resulting picture files into tomcat8 webapp dir
#
import time
import picamera
import shutil
import os

with picamera.PiCamera() as camera:
    camera.resolution = (100, 100)
    #####camera.use_video_port=True
    #camera.start_preview()
    # Camera warm-up time
    print "warming up"
    time.sleep(2)
    print "done warming up"
    vaar = 1
    count=0
    while vaar == 1:
       count=count+1
       if count < 0:
          count=0

       print "%d" %count 
       #camera.capture_sequence([
       #   '/pi/apache-tomcat-7.0.62/webapps/rap-web/img.jpg'])
       #camera.capture('/home/pi/apache-tomcat-7.0.62/webapps/rap-web/img.jpg')
       camera.capture_sequence([
          '/var/lib/tomcat8/webapps/rap-web/img.jpg'],use_video_port=True)
       time.sleep(1)
       print "end seq"
       fsize = os.path.getsize('/var/lib/tomcat8/webapps/rap-web/img.jpg')
       if fsize < 1: 
            print ("file size was 0")
            time.sleep(2) 
