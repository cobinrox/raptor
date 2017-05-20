#
# Read two GPIO buttons which  can be used to set up
# to represent various states
#
# 00 dhcp
# 01 static IP
# 10 hot spot
# 11 not used
#
import time
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# button1 = 4 # does not work
# button1 = 21
button1 = 17 #works
button2 = 22 #works

b1=0
b2=0

# set up pins with pull down resistor, resulting
# in default low = button not pressed
#
GPIO.setup(button1, GPIO.IN, GPIO.PUD_DOWN)
GPIO.setup(button2, GPIO.IN, GPIO.PUD_DOWN)

print ("Checking BCM button 1")
print ("Checking BCM button 2")
while True:
  b1=0
  b2=0
  button1_state = GPIO.input(button1)
  button2_state = GPIO.input(button2)
  if button1_state == GPIO.HIGH:
    print ("button1: HIGH ( PRESSED!!!!!!!!!!")
    b1=1
  else:
    print ("button1: LOW (not pressed)")
    b1=0
  if button2_state == GPIO.HIGH:
    print ("button2: HIGH (PRESSED !!!!!")
    b2=1
  else:
    print ("button2: LOW (not pressed)")
    b2=0
  if b1==0 and b2==0:
    print ("state 00")
  elif b1==1 and b2==0:
    print ("state 01")
  elif b1==0 and b2==1:
    print ("state 10")
  elif b1==1 and b2==1:
    print ("state 11")
  time.sleep(5)