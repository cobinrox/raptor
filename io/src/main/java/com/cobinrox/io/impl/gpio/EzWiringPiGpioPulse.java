package com.cobinrox.io.impl.gpio;

import org.apache.log4j.Logger;

import java.net.InetAddress;

/**
 * Created by robin.cox on 11/15/2015.
 */
public class EzWiringPiGpioPulse
{
    static final Logger logger = Logger.getLogger(EzWiringPiGpioPulse.class);
    public static boolean simulatePi ;
    static Runtime g_rt = Runtime.getRuntime();
    public static int testPinNum = 3;
    public static void main(String[] args) throws Throwable
    {
        EzWiringPiGpioPulse.simulatePi = true;

        try
        {
            if (args!=null && args.length > 0)
            {
                testPinNum = Integer.parseInt(args[0]);
            }
        }
        catch(Throwable t)
        {

        }
        if( args.length != 0 && args.length > 0)
        {
            EzWiringPiGpioPulse.simulatePi = false;
        }

        logger.info("USING WIRING PI PIN NUM [" + testPinNum + "]");
        EzWiringPiGpioPulse ep = new EzWiringPiGpioPulse();
for (int j=0; j<50; j++)
        {
            InetAddress IP=InetAddress.getLocalHost();
            String addr = IP.getHostAddress();
            System.out.println("IP of my system is := "+addr);
            String[] dots = addr.split("\\.");
            int a = Integer.parseInt(dots[0]);
            int b = Integer.parseInt(dots[1]);
            int c = Integer.parseInt(dots[2]);
            int d = Integer.parseInt(dots[3]);

            String ccc = String.format ("%03d", c);
            String ddd = String.format ("%03d", d);

            int c1 = Integer.parseInt(ccc.substring(0, 1));
            int c2 = Integer.parseInt(ccc.substring(1,2));
            int c3 = Integer.parseInt(ccc.substring(2,3));

            int d1 = Integer.parseInt(ddd.substring(0,1));
            int d2 = Integer.parseInt(ddd.substring(1,2));
            int d3 = Integer.parseInt(ddd.substring(2,3));

            ep.blinkAttention(testPinNum);
            for (int i = 0; i <c1; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }
            Thread.currentThread().sleep(3000);

            for (int i = 0; i <c2; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }

            for (int i = 0; i <c3; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }
            Thread.currentThread().sleep(3000);

            for (int i = 0; i <d1; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }
            Thread.currentThread().sleep(3000);

            for (int i = 0; i <d2; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }

            for (int i = 0; i <d3; i++)
            {
                ep.lowLevelPulseOn(testPinNum);
                Thread.currentThread().sleep(1000);
                ep.lowLevelPulseOff(testPinNum);
            }
            Thread.currentThread().sleep(3000);
        }
    }

    public void blinkAttention(int pinNum) throws Throwable
    {
        for(int i=0; i < 1; i++)
        {
             lowLevelPulseOn(testPinNum);
            Thread.currentThread().sleep(10000);
             lowLevelPulseOff(testPinNum);
        }
    }
    public void lowLevelPulseOn(final int pinNum) throws Throwable{
        logger.debug("          low level gpio ON request for [" + pinNum + "]"  );
        Process p = null;
        String gCmd = null;
        String m1Info = null;
        gCmd = "gpio write " + pinNum + " 1";

        logger.info("Sending cmd [" + gCmd + "]");
        if (!simulatePi) {
            p = g_rt.exec(gCmd);
            p.waitFor();
        }
        else
        {
            System.out.print("ON ");
        }
    }

    public void lowLevelPulseOff(final int pinNum) throws Throwable{
        logger.debug("          low level gpio OFF request for [" + pinNum + "]"  );
        Process p = null;
        String gCmd = null;
        String m1Info = null;
        gCmd = "gpio write " + pinNum + " 0";

        logger.info("Sending cmd [" + gCmd + "]");
        if (!simulatePi) {
            p = g_rt.exec(gCmd);
            p.waitFor();
        }
        else
        {
            System.out.print (" OFF");
        }
    }

}
