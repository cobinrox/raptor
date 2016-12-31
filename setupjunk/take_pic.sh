# easy but cheesey way to take a snapshot via raspicam
# takes up to 4 seconds to get an image so this is not
# overly useful for quick image turn around, as is needed
# for raptor
#
SAVEDIR=/home/pi/apache-tomcat-7.0.62/webapps/rap-web
while [ true ]; do
filename=img.jpg
raspistill -w 300 -h 300 -o $SAVEDIR/$filename
sleep 4;
done; 
