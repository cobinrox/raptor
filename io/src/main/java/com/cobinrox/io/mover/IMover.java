package com.cobinrox.io.mover;

import com.cobinrox.io.impl.MotorProps;

import java.util.Properties;

public interface IMover
{
   public void init(MotorProps p) throws Throwable;

    /**
     * Execute command, this is primarily used to issue a move command such as F, L, R, B, but can be other
     * commands as well such as read a value from the lower-level hardware, or set an, dependent on back-end hw implementation.
     * The uiCmd will be in the form as read from the motor properties file which looks something like:
     * F:+1
     * or
     * R:-0.25
     * or
     * RAMP
     * @param uiCmd
     * @return
     */
   public String execute(String uiCmd);

    /**
     * Indicate to the mover that the caller changed a value in the properties, the implementation may therefore
     * need to re-initialize or may have to do someother implementation-dependent act
     * @param key
     * @param val
     * @return
     */
   public String dataChanged(String key, String val);
   public void shutdown();
   public void setEbrake(boolean e);
}
   