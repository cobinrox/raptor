package com.cobinrox.io.mover;

import com.cobinrox.io.impl.MotorProps;

import java.util.Properties;

public interface IMover
{
   public void init(MotorProps p) throws Throwable;
   public String move(String uiCmd);
   public String dataChanged(String key, String val);
   public void shutdown();
   public void setEbrake(boolean e);
}
   