package com.cobinrox.io.impl.gpio;

import com.cobinrox.io.impl.MotorProps;
import org.apache.log4j.Logger;

//import com.cobinrox.io.impl.MotorProps;

public class WiringPiMotorImpl extends AbstractGPIOMotorImpl {
	static final Logger logger = Logger.getLogger(WiringPiMotorImpl.class);
	Runtime g_rt = Runtime.getRuntime();
	String g_debugStatusString;
	Thread g_debugStatusThread;
	boolean debugStatus = false;

	public WiringPiMotorImpl(String alias) {
		super(alias);
	}

	@Override
	public void init(MotorProps mp) throws Throwable {
		super.init(mp);
		logger.info("Initializing pins to OUTPUT MODE for wiringpi");
		try {
				String cmd = "gpio mode " + super.plusPinNum + " out";
			    logger.info("preparing cmd [" + cmd + "]...");
			    Process p = null;
			    if( !mp.simulate_pi ) {
					logger.info("sending cmd");
					p = g_rt.exec("gpio mode " + super.plusPinNum + " out");
					p.waitFor();
				}
			    else
				{
					logger.info("simulating sending cmd");
				}
			    cmd = "gpio mode " + super.minusPinNum + " out";
     			logger.info("preparing cmd [" + cmd + "]...");

	    		if( !mp.simulate_pi ) {
					logger.info("sending cmd");
					p = g_rt.exec("gpio mode " + super.minusPinNum + " out");
					p.waitFor();
				}

				brakeAll();

		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR setting pin modes!  Cannot run!");
			return;
		}
	}

	//@Override
	//protected void hwInit()
	{
		logger.info("hwInit for WiringPiMotorImpl not implemented");
	}

	private void runDebugStatusThread() {
		if(!debugStatus) return;
		g_debugStatusThread = new Thread() {
			public void run() {
				setName(alias+"debugStatusThrd");
				int i = 0;
				while (true) {
					if( g_debugStatusString != null ) {
						System.out.print("[" + g_debugStatusString + (i+1) + "]");
						i++;
						if (i > 80) {
							i = 0;
							System.out.println("");
						}
					}
					try
					{
					sleep(1);}catch(Throwable t){}
				}
			}
		};
	}

String gCmd;
	public void lowLevelEnableDisable(final String d) throws Throwable{
		logger.debug("          low level enable/disable request [" + alias +  " E" + d + "]"  );
		Process p = null;
		gCmd = null;

		// note we assume the secondary pin is the direction pin
		gCmd = "gpio write " + super.minusPinNum;
				//+ (d.equals("+") ?
				//super.plusPinNum
				//:
				//super.minusPinNum);
		g_debugStatusString = alias + d;

		String onCmd = gCmd + " " +
				(d.equals("+") ?super.mp.gpio_on:super.mp.gpio_off);
		if (!mp.simulate_pi) {
			logger.debug("          Sending cmd [" + onCmd + "]");
			p = g_rt.exec(onCmd);
			p.waitFor();
		}
		else
		{
			logger.debug("Sending cmd [" + onCmd + "] (SIMULATED)");
		}
		//kind of annoying logger.info("          ...end low level execute");
	}
	public void lowLevelPulse(final String plusOrMinusDirChar, final float notUsed) throws Throwable{
		logger.debug("          low level execute request [" + alias +  plusOrMinusDirChar + "]"  );
		Process p = null;
		gCmd = null;
		String m1Info = null;
		gCmd = "gpio write "
					+ (plusOrMinusDirChar.equals("+") ?
					super.plusPinNum
					:
					super.minusPinNum);
			g_debugStatusString = alias + plusOrMinusDirChar;
			//if (g_debugStatusThread == null ){ runDebugStatusThread(); if(g_debugStatusThread != null) g_debugStatusThread.start();}


		String pulseOnCmd = gCmd + " " + super.mp.gpio_on;
		logger.debug("Sending cmd [" + pulseOnCmd + "]");
		if (!mp.simulate_pi) {
			p = g_rt.exec(pulseOnCmd);
			p.waitFor();
		}
	}

	public void lowLevelStop(final String plusOrMinusDirChar, final float notUsed) throws Throwable {
		logger.debug("          low level stop request [" +alias + plusOrMinusDirChar + "]"  );
		Process p = null;
		gCmd = null;
		gCmd = "gpio write "
				+ (plusOrMinusDirChar.equals("+") ?
				super.plusPinNum
				:
				super.minusPinNum);
		g_debugStatusString = alias.toLowerCase() + plusOrMinusDirChar;
		//if (g_debugStatusThread == null ){ runDebugStatusThread(); if(g_debugStatusThread != null) g_debugStatusThread.start();}

		String pulseOffCmd = gCmd + " " + mp.gpio_off;
		logger.debug("          Sending cmd [" + pulseOffCmd + "]");
		if (!mp.simulate_pi) {
			p = g_rt.exec(pulseOffCmd);
			p.waitFor();
		}
	}


	public void brakeAll() {
		Process p = null;
		try {
			long start = new java.util.Date().getTime();
			logger.debug("     Shutdown (brake) all pins...");
			String cmd = "gpio write " + super.plusPinNum + " " + mp.gpio_off;
			logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
			if (!mp.simulate_pi) {
				p = g_rt.exec(cmd);
				p.waitFor();
			}
			long stop = new java.util.Date().getTime();
			long dur = stop - start;
			logger.debug("stop " + stop + " dur " + dur);
			cmd = "gpio write " + super.minusPinNum + " " + mp.gpio_off;
			logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
			if (!mp.simulate_pi) {
				p = g_rt.exec(cmd);
				p.waitFor();
			}

			logger.debug("     ...end shut down all pins");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR CLEARING ALL");
		}
		try {
			g_debugStatusString = null;

		} catch (Throwable t) {
logger.error("hork");
		}
	}
}
