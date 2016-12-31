#!/bin/bash
# turn off GPIO pins, to be used at boot time but does not
# work fast, so do not rely on this for motor control!
# ok for leds
# alternative: look at Device Tree Source capability
#
/usr/local/bin/gpio mode 1 out
/usr/local/bin/gpio write 1 0

/usr/local/bin/gpio mode 4 out
/usr/local/bin/gpio write 4 0

/usr/local/bin/gpio mode 5 out
/usr/local/bin/gpio write 5 0

/usr/local/bin/gpio mode 6 out
/usr/local/bin/gpio write 6 0

/usr/local/bin/gpio mode 7 out
/usr/local/bin/gpio write 7 0

