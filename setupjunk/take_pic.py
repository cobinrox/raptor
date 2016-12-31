# take picture using pi camera video stream and python library
# much faster snapshots can be taken using this script
import time
import picamera
import shutil

with picamera.PiCamera() as camera:
    camera.resolution = (100, 100)
    camera.use_video_port=True
    #camera.start_preview()
    # Camera warm-up time
    print "warning up"
    time.sleep(2)
    print "done warming up"
    vaar = 1
    while vaar == 1:
       print "while"
       #camera.capture_sequence([
       #   '/pi/apache-tomcat-7.0.62/webapps/rap-web/img.jpg'])
       #camera.capture('/home/pi/apache-tomcat-7.0.62/webapps/rap-web/img.jpg')
       camera.capture_sequence([
          '/home/pi/wtf.jpg'],use_video_port=True)
       #for filename in camera.capture_continuous('wtf.jpg'):
       #print "copying"
       shutil.copy2('wtf.jpg','/home/pi/apache-tomcat-7.0.62/webapps/rap-web/img.jpg')
       print "end seq"
       #time.sleep(1)
