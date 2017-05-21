package com.cobinrox.io;


import com.cobinrox.io.impl.MotorProps;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	static final Logger logger = Logger.getLogger(AppTest.class);
	public void testDummy(){}
    public void atestDoMotorCmd() throws Throwable
    {
    	DoMotorCmd d = new DoMotorCmd();
    	d.doThis("f");
    	d.shutdown();
    			
    }

	public void testColonParse() throws Throwable{
		String str = "F:+1|-1,LEFT_TURN:'MD:0'|'MT:-512',L:-1";
		HashMap<String,List<String>> hm = MotorProps.parseHighLevelMotorCmdsToLowLevelDTs(str,false);
		System.out.println("check ");
	}

	public void testIncCyclePercent() throws Throwable{
		String mAlias = "m1";

		// 50 percent
		int percentage = 50;
		int cycleTime = 500;
		MotorProps mp = new MotorProps();
		MotorProps.lowlevelSetDutyHiPercent(mAlias,percentage,cycleTime);
System.out.println("duty hi: " + mp.m1_duty_cycle_hi_ms);
System.out.println("duty lo: " + mp.m1_duty_cycle_lo_ms);
		assertEquals(mp.m1_duty_cycle_hi_ms,250);
		assertEquals(mp.m1_duty_cycle_lo_ms,250);


		// 100 percent
		 percentage = 100;
		 cycleTime = 500;
		MotorProps.lowlevelSetDutyHiPercent(mAlias,percentage,cycleTime);
		System.out.println("duty hi: " + mp.m1_duty_cycle_hi_ms);
		System.out.println("duty lo: " + mp.m1_duty_cycle_lo_ms);
		assertEquals(mp.m1_duty_cycle_hi_ms,500);
		assertEquals(mp.m1_duty_cycle_lo_ms,0);

	}
}
