package com.cobinrox.io.impl.gpio;
import com.cobinrox.io.impl.IMotor;
import com.cobinrox.io.impl.MotorProps;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * High-level implementation for GPIO control of motors.
 * Lower-level implementation classes would provide lower-level hardware driver
 * details, e.g. implementation using the pi4j library or, for example, the
 * WiringPi/python library
 */
public abstract class AbstractGPIOMotorImpl implements IMotor {
    static final Logger logger = Logger.getLogger(AbstractGPIOMotorImpl.class);

    /**
     * Gpio pin number for M+ and M- connections
     */
    String plusPinNum;
    String minusPinNum;

    /**
     * Motor alias, e.g. M1, M2, maps to properties file
     */
    String alias;

    /**
     * Emergency brake flag, static and public so that anyone can call in case of
     * emergency
     */
    static public boolean ebrake;

    /**
     * When executing a execute (e.g. Forward/Backward, etc.) the movement can actually
     * consist of multiple lower-level sub-commands, like, e.g.:
     * F:+1|+1|+1 which contains 3 sub-commands
     */
    int numCmdsToExpect;

    /**
     * Keeps track of number of sub-commands that have finished;
     */
    public int numFinishedCommands;

    /**
     * Properties from io properties file
     */
    MotorProps mp;

    /**
     * Constructor
     * @param alias prefix used to identify this motor, maps to properties
     */
    public AbstractGPIOMotorImpl(String alias) {
        this.alias = alias;
    }

   @Override
    public void setEbrake(boolean brake)
    {
        ebrake = brake;
        logger.info("**** EBRAKE REQUESTED ****");
    }
    @Override
    public boolean getEbrake(){ return ebrake;}

    @Override
    public void init(MotorProps mp) throws Throwable {
        this.mp = mp;
        plusPinNum    = (String) mp.get(alias + "_PLUS_GPIO_PIN");
        minusPinNum   = (String) mp.get(alias + "_MINUS_GPIO_PIN");
        //hwInit();
    }

    @Override
    abstract public void brakeAll() ;

    @Override
    public void setExpectedNumCmds(int numCmdsToExpect) {
        this.numCmdsToExpect = numCmdsToExpect;
        this.numFinishedCommands = 0;
    }

    @Override
    public int getNumFinishedCommands() {
        return numFinishedCommands;
    }

    /**
     * @param  pulseCmds array of strings example 2-element value: "+,1", "-,1",
     *             HACK ALERT HACK ALERT: NOW INCLUDING THIS: "+,E", "-,E"
     *             TO HANDLE DIFFERENT TYPE OF I/O BOARD THAN TYPICAL L293D
     *             SO YOU WOULD NEED TO DO SOMETHING LIKE THIS FOR A FORWARD MOVEMENT
     *             FOR L293D: F:+1,B:-1
     *             FOR ENIO:  F:+E|+1,B:-E|+1
     * @return
     */
    @Override
    public String nonBlockingPulse( final List<String> pulseCmds, final int cmd_run_time_ms, final int duty_cycle_hi_ms,
                                    final int duty_cycle_lo_ms ) {

        Thread thrd = new Thread() {
            public void run() {
                next_cmd:
                for(int pulseCmdIdx=0; pulseCmdIdx < pulseCmds.size();pulseCmdIdx++) {
                    if(ebrake)
                    {
                        logger.error("---------------------EBRAKE!-------------------------");
                        brakeAll();
                        numFinishedCommands = numCmdsToExpect;
                        return;
                    }
                    String[]thisCmd = pulseCmds.get(pulseCmdIdx).split(",");
                    logger.info("----------------debug thisCmd size/0/1: " + thisCmd.length + "/" + thisCmd[0] + "/" + thisCmd[1]);
                    String plusOrMinusDirChar = thisCmd[0];
                    // HACK ALERT HACK ALERT!
                    if( thisCmd[1].equals("E"))
                    {
                        //logger.info("     (hackalert) Enabling Direction [" + thisCmd[0] +"]");
                        setName( alias + (plusOrMinusDirChar.equals("+")?"FWD":"BACK") );
                        logger.info("     Enabling Direction [" + plusOrMinusDirChar +"] via pin [" + minusPinNum + "]");
                        try
                        {
                            lowLevelEnableDisable(thisCmd[0]);
                            //System.out.println("?????");
                            // MAJOR HACK ALERT!!!
                            logger.debug("     (bumping numFinishedCmds " + numFinishedCommands + ")");
                            numFinishedCommands++;
                        }
                        catch(Throwable t)
                        {
                            t.printStackTrace();
                            String e = "ERROR ENABLING " + alias + "[" + thisCmd[0] + "," + thisCmd[1] + "]";
                            System.err.println(e);
                            ebrake = true;
                        }
                        continue next_cmd;
                    }

                    float t = 0;
                    if( !thisCmd[1].equals("NOP"))
                    {
                        t = Float.parseFloat(thisCmd[1]);
                    }
                    // allow command to run as percentage of normal hi dutycycle
                    // e.g.  L: -0.25 would be run at only 25% of hi duty cycle, this is to allow for
                    // tweaking L/R steering commands
                    int numMsHi = (int) (duty_cycle_hi_ms * t);
                    //int numMsLo = (int) (duty_cycle_lo_ms);
                    int numMsLo = cmd_run_time_ms - numMsHi;
                    //logger.info("-------------------------debug t/num hi/low: " + t + "/" + numMsHi + "/" + numMsLo);
                    long outerStart = System.currentTimeMillis();
                    setName(alias + plusOrMinusDirChar );
                    try {
                        logger.info("     Pulsing [" + plusOrMinusDirChar +"], pulses across ["
                                + cmd_run_time_ms + "] ms ...");
                        long elapsedTime = cmd_run_time_ms;
                        while( elapsedTime > 0 )
                        {
                            long startPulseAt = System.currentTimeMillis();
                            if(ebrake)
                            {
                                logger.error("---------------------EBRAKE!-------------------------");
                                brakeAll();
                                numFinishedCommands = numCmdsToExpect;
                                return;
                            }
                            logger.info("     turning ON for [" + numMsHi + "]ms...");
                            if( t != 0 )lowLevelPulse(plusOrMinusDirChar, t);
                            Thread.sleep(numMsHi);

                            if (numMsLo > 0) {
                                logger.info("     turning OFF for [" + numMsLo + "]ms...");
                                if(t != 0 )lowLevelStop(plusOrMinusDirChar, t);
                                Thread.sleep(numMsLo);
                            }
                            long stopPulseAt = System.currentTimeMillis();
                            elapsedTime = elapsedTime - (stopPulseAt-startPulseAt);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                        System.err.println("Error pulsing gpio pin");
                    } finally {
                        long outerStop = System.currentTimeMillis();
                        logger.debug("     ...end pulsing; total time sending cmd: "
                                + (outerStop - outerStart) + " ms");
                    }

                    logger.debug("     (bumping numFinishedCmds " + numFinishedCommands + ")");
                    numFinishedCommands++;
                }
            }
        };

        thrd.start();
        return(alias + " nonBlockingPulse started");
    }

    /**
     * Can be used to enable/disable hw board
     * @param d value to use as disable/enable flag
     * @throws Throwable
     */
    abstract protected void lowLevelEnableDisable(String d) throws Throwable;

    /**
     * Low-level implementation for harwdware to send value
     * @param d direction
     * @param t value
     * @throws Throwable
     */
    abstract protected void lowLevelPulse(String d, float t) throws Throwable;

    /**
     * Low-level stop implementation
     * @param d direction
     * @param t value
     * @throws Throwable
     */
    abstract public void lowLevelStop(final String d, final float t) throws Throwable;
}