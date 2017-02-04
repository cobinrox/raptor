package com.cobinrox.io.impl.gpio;
import com.cobinrox.io.impl.IMotor;
import com.cobinrox.io.impl.MotorProps;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

public abstract class AbstractGPIOMotorImpl implements IMotor {
    static final Logger logger = Logger.getLogger(AbstractGPIOMotorImpl.class);
    DecimalFormat df = new DecimalFormat("###.#");
    String plusPinNum;
    String minusPinNum;
    String alias;
    static public boolean ebrake;

    public int numFinishedThreads;
    int numCmdsToExpect;

    MotorProps mp;
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
    /*
    public void reInitCycleLens(long hiCycleLenMs, long lowCycleLenMs, long totalCmdLenMs) {
        //this.hiCycleLenMs = hiCycleLenMs;
        //this.lowCycleLenMs = lowCycleLenMs;
        //this.totalCmdLenMs = totalCmdLenMs;

    }

    public void reInitPinNums(String plusPin, String minusPin) {
        this.plusPinNum = plusPin;
        this.minusPinNum = minusPin;
        hwInit();
    }
*/
    @Override
    public void init(MotorProps mp) throws Throwable {
        this.mp = mp;
        plusPinNum    = (String) mp.get(alias + "_PLUS_GPIO_PIN");
        minusPinNum   = (String) mp.get(alias + "_MINUS_GPIO_PIN");
        //hwInit();
    }

    //abstract protected void hwInit();// {
        // set GPIO pin outs
    //}

    @Override
    abstract public void brakeAll() ;//{

    //}

    @Override
    public void setExpectedNumCmds(int numCmdsToExpect) {
        this.numCmdsToExpect = numCmdsToExpect;
        this.numFinishedThreads = 0;
    }

    @Override
    public int getNumFinishedCommands() {
        return numFinishedThreads;
    }

    /*
    public void enableDisableDirPin(String plusOrMinus) throws Throwable
    {
        lowLevelPulse(plusOrMinus, 0);
    }*/
    /**
     * @param  pulseCmds array of strings example 2-element value: "+,1", "-,1",
     *             HACK ALERT HACK ALERT: NOW INCLUDING THIS: "+,E", "-,E"
     *             TO HANDLE DIFFERENT TYPE OF I/O BOARD THAN TYPICAL L293D
     *             SO YOU WOULD NEED TO DO SOMETHING LIKE THIS FOR A FORWARD MOVEMENT
     *             FOR L293D: F:+1,B:-1
     *             FOR ENIO:  F:+E|+1,B:-E|+1
     * @return
     * @deprecated
     */
    @Override
    public String nonBlockingPulse( final List<String> pulseCmds, final int cmd_run_time_ms, final int duty_cycle_hi_ms, final int duty_cycle_lo_ms ) {

        Thread thrd = new Thread() {
            public void run() {
                next_cmd:
                for(int pulseCmdIdx=0; pulseCmdIdx < pulseCmds.size();pulseCmdIdx++) {
                    if(ebrake)
                    {
                        logger.error("---------------------EBRAKE!-------------------------");
                        brakeAll();
                        numFinishedThreads = numCmdsToExpect;
                        return;
                    }
                    String[]thisCmd = pulseCmds.get(pulseCmdIdx).split(",");
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
                            logger.debug("     (bumping numFinishedCmds " + numFinishedThreads + ")");
                            numFinishedThreads++;
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

                    float t = Float.parseFloat(thisCmd[1]);
                    int numMsHi = (int) (duty_cycle_hi_ms * t);//utyCycleHi;//dutyCycleHi;//(int)((float)numMsToRunCmdFor * (float)((float)dutyCycleHi/(float)100));
                    int numMsLo = (int) (duty_cycle_lo_ms);
                    long outerStart = System.currentTimeMillis();
                    setName(alias + plusOrMinusDirChar );
                    try {
                        int numPeriods = (int) ((float) cmd_run_time_ms / ((float) numMsHi + (float) numMsLo));
                        //logger.info("     Pulsing [" + plusOrMinusDirChar +"], need [" + numPeriods + "] pulses across ["
                        //        + cmd_run_time_ms + "] ms ...");
                        logger.info("     Pulsing [" + plusOrMinusDirChar +"], pulses across ["
                                + cmd_run_time_ms + "] ms ...");
                        long elapsedTime = cmd_run_time_ms;
                        //for (int i = 0; i < numPeriods; i++)
                        while( elapsedTime > 0 )
                        {
                            long startPulseAt = System.currentTimeMillis();
                            if(ebrake)
                            {
                                logger.error("---------------------EBRAKE!-------------------------");
                                brakeAll();
                                numFinishedThreads = numCmdsToExpect;
                                return;
                            }
                            logger.info("     turning on for [" + numMsHi + "]ms...");
                            lowLevelPulse(plusOrMinusDirChar, t);
                            Thread.sleep(numMsHi);

                            if (numMsLo > 0) {
                                logger.info("     turning off for [" + numMsLo + "]ms...");
                                lowLevelStop(plusOrMinusDirChar, t);
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

                    logger.debug("     (bumping numFinishedCmds " + numFinishedThreads + ")");
                    numFinishedThreads++;
                }
            }
        };

        thrd.start();
        return(alias + " nonBlockingPulse started");
    }

    abstract protected void lowLevelEnableDisable(String d) throws Throwable;// {
    //}
    abstract protected void lowLevelPulse(String d, float t) throws Throwable;//{
   /// }

    abstract public void lowLevelStop(final String d, final float t) throws Throwable;// {
    //}
}