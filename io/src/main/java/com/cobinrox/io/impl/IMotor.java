package com.cobinrox.io.impl ;


import java.util.List;

public interface IMotor
{
   public void init(MotorProps mp) throws Throwable;
   public void setExpectedNumCmds(int numCmds);
   public int getNumFinishedCommands();
   //public String nonBlockingPulse(List<String> dTs);
   public String nonBlockingPulse(List<String> dTs, int cmdRunTime, int dutyHi, int dutyLo);
   public void brakeAll();
   public void setEbrake(boolean brake);
}