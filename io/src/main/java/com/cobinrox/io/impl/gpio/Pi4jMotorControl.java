package com.cobinrox.io.impl.gpio;

import com.cobinrox.io.impl.MotorProps;
import org.apache.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.Map;
import java.util.Set;

public class Pi4jMotorControl extends AbstractGPIOMotorImpl {
    static final Logger logger = Logger.getLogger(Pi4jMotorControl.class);

    GpioPinDigitalOutput pi4jPlusPin;
    GpioPinDigitalOutput pi4jMinusPin;
    static GpioController pi4jController;

    public Pi4jMotorControl(String alias) {
        super(alias);
    }

    @Override
    public void init(MotorProps mp) throws Throwable {
        super.init(mp);
        logger.info("Initializing pins to OUTPUT MODE for pi4j");
        if( pi4jController == null ) {
            pi4jController = GpioFactory.getInstance();
        }

        if (!mp.simulate_pi) {
            try {
                pi4jController = GpioFactory.getInstance();
                PinState pinoff = mp.gpio_on == 1 ? PinState.HIGH : PinState.LOW;
                int thingToSwitch = alias.equals("M1")?mp.m1_PLUS_GPIO_PIN:mp.m2_PLUS_GPIO_PIN;
                Pin pi4jPinNumPlus = null;
                Pin pi4jPinNumMinus = null;
                switch (thingToSwitch) {
                    case 0:
                        pi4jPinNumPlus = RaspiPin.GPIO_00; break;
                    case 1:
                        pi4jPinNumPlus = RaspiPin.GPIO_01;break;
                    case 2:
                        pi4jPinNumPlus = RaspiPin.GPIO_02; break;
                    case 3:
                        pi4jPinNumPlus = RaspiPin.GPIO_03;break;
                    case 4:
                        pi4jPinNumPlus = RaspiPin.GPIO_04;break;
                    case 5:
                        pi4jPinNumPlus = RaspiPin.GPIO_05;break;
                    case 6:
                        pi4jPinNumPlus = RaspiPin.GPIO_06;break;
                    case 7:
                        pi4jPinNumPlus = RaspiPin.GPIO_07;break;
                    case 8:
                        pi4jPinNumPlus = RaspiPin.GPIO_08;break;
                    case 9:
                        pi4jPinNumPlus = RaspiPin.GPIO_09;break;
                    case 10:
                        pi4jPinNumPlus = RaspiPin.GPIO_10;break;
                    case 11:
                        pi4jPinNumPlus = RaspiPin.GPIO_11;break;
                    case 12:
                        pi4jPinNumPlus = RaspiPin.GPIO_12;break;
                    case 13:
                        pi4jPinNumPlus = RaspiPin.GPIO_13;break;
                    case 14:
                        pi4jPinNumPlus = RaspiPin.GPIO_14;break;
                    case 15:
                        pi4jPinNumPlus = RaspiPin.GPIO_15;break;
                    case 16:
                        pi4jPinNumPlus = RaspiPin.GPIO_16;break;
                    case 17:
                        pi4jPinNumPlus = RaspiPin.GPIO_17;break;
                    case 18:
                        pi4jPinNumPlus = RaspiPin.GPIO_18;break;
                    case 19:
                        pi4jPinNumPlus = RaspiPin.GPIO_19;break;
                    case 20:
                        pi4jPinNumPlus = RaspiPin.GPIO_20;break;
                }

                thingToSwitch = alias.equals("M1")?mp.m1_MINUS_GPIO_PIN:mp.m2_MINUS_GPIO_PIN;
                switch (thingToSwitch) {
                    case 0:
                        pi4jPinNumMinus = RaspiPin.GPIO_00; break;
                    case 1:
                        pi4jPinNumMinus = RaspiPin.GPIO_01;break;
                    case 2:
                        pi4jPinNumMinus = RaspiPin.GPIO_02; break;
                    case 3:
                        pi4jPinNumMinus = RaspiPin.GPIO_03;break;
                    case 4:
                        pi4jPinNumMinus = RaspiPin.GPIO_04;break;
                    case 5:
                        pi4jPinNumMinus = RaspiPin.GPIO_05;break;
                    case 6:
                        pi4jPinNumMinus = RaspiPin.GPIO_06;break;
                    case 7:
                        pi4jPinNumMinus = RaspiPin.GPIO_07;break;
                    case 8:
                        pi4jPinNumMinus = RaspiPin.GPIO_08;break;
                    case 9:
                        pi4jPinNumMinus = RaspiPin.GPIO_09;break;
                    case 10:
                        pi4jPinNumMinus = RaspiPin.GPIO_10;break;
                    case 11:
                        pi4jPinNumMinus = RaspiPin.GPIO_11;break;
                    case 12:
                        pi4jPinNumMinus = RaspiPin.GPIO_12;break;
                    case 13:
                        pi4jPinNumMinus = RaspiPin.GPIO_13;break;
                    case 14:
                        pi4jPinNumMinus = RaspiPin.GPIO_14;break;
                    case 15:
                        pi4jPinNumMinus = RaspiPin.GPIO_15;break;
                    case 16:
                        pi4jPinNumMinus = RaspiPin.GPIO_16;break;
                    case 17:
                        pi4jPinNumMinus = RaspiPin.GPIO_17;break;
                    case 18:
                        pi4jPinNumMinus = RaspiPin.GPIO_18;break;
                    case 19:
                        pi4jPinNumMinus = RaspiPin.GPIO_19;break;
                    case 20:
                        pi4jPinNumMinus = RaspiPin.GPIO_20;break;
                }
                try {
                    if( pi4jPlusPin != null )
                    {
                        logger.info("Plus Pin already provisioned" + alias);
                        // doesn't work pi4jController.unprovisionPin(pi4jPlusPin);
                        return;
                    }
                    pi4jPlusPin = pi4jController.provisionDigitalOutputPin(pi4jPinNumPlus, alias + "+", pinoff);
                    //pi4jController.
                }
                catch(Throwable t)
                {
                    logger.error("Provisioning pin " + pi4jPinNumPlus,t );
                }
                if(pi4jPlusPin == null )
                {
                    logger.error("Could not set Pi4jPlusPin for pin num [" + pi4jPinNumPlus + "] for alias [" + alias + "]");
                }

                try {
                    if( pi4jMinusPin != null )
                    {
                        logger.error("Minus Pin already provisioned");
                        return;
                        //doesn't work pi4jController.unprovisionPin(pi4jPlusPin);
                    }
                    pi4jMinusPin = pi4jController.provisionDigitalOutputPin(pi4jPinNumMinus, alias + "-", pinoff);
                }
                catch(Throwable t)
                {
                    logger.error("Provisioning pin " + pi4jPinNumMinus,t );
                }
                if(pi4jMinusPin == null )
                {
                    logger.error("Could not set pi4jMinusPin for pin num [" + pi4jPinNumMinus + "] for [" + alias + "]");
                }

                if( pi4jPinNumPlus != null && pi4jPinNumMinus != null)
                {
                    pi4jMinusPin.setState(PinState.LOW);
                    pi4jPlusPin.setState(PinState.LOW);

                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.err.println("ERROR setting pin modes!  Cannot run!");
                return;
            }
        } else {
            logger.info("Simulated setting pins to OUTPUT MODE");
        }
    }

    //@Override
    //protected void hwInit()
    //{
    //    logger.info("hwInit for Pi4jMotorControl class not implemented");
    //}

    /**
     *
     * @param d direction character + or -
     * @param t time value 1, 2, etc. num times to pulse, normally set to 1
     * @throws Throwable
     */
    public void lowLevelPulse(final String d, final float t) throws Throwable {
        logger.debug("          low level move request [" + alias + d + "]");

        String m1Info = null;
        if (d != null) {
            m1Info = " (m1 "+ d+")";
            if (!mp.simulate_pi) {
                if (d.equals("+")) {
                    pi4jPlusPin.high();
                } else {
                    pi4jMinusPin.high();
                }
            }

        }
    }
    public void lowLevelEnableDisable(final String d) throws Throwable{
        logger.debug("          low level enable/disable request [" + alias +  " E" + d + "]"  );
        if (d != null) {
            if (!mp.simulate_pi) {
                if (d.equals("+")) {
                    pi4jMinusPin.high();
                } else {
                    pi4jMinusPin.low();
                }
            }

        }

    }

    public void lowLevelStop(final String d, final float t) throws Throwable {
        logger.info("low level stop "+ alias + d);
        if (!mp.simulate_pi) {
            pi4jPlusPin.low();
            pi4jMinusPin.low();
        }
    }

    public void brakeAll() {
        if (!mp.simulate_pi) {
            //logger.info("DEBUG NPE?" + " " + (pi4jPlusPin==null?"NULL?!!":"no"));
            pi4jPlusPin.low();
            pi4jMinusPin.low();
        } else {
            logger.info("Simulating shut down all pins");
        }
    }

    public void shutdown() throws Throwable {
        brakeAll();
    }
}
