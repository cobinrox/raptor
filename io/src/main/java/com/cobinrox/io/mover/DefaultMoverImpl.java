package com.cobinrox.io.mover;

import com.cobinrox.io.impl.IMotor;
import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.impl.gpio.Pi4jMotorControl;
import com.cobinrox.io.impl.gpio.WiringPiMotorImpl;
import com.cobinrox.io.impl.usb.Usb4jMotorControl;
import org.apache.log4j.Logger;

import java.util.List;

public class DefaultMoverImpl implements IMover
{
   static final Logger logger = Logger.getLogger(DefaultMoverImpl.class);

   IMotor m1;
   IMotor m2;
   MotorProps mp;

   @Override
   public void setEbrake(boolean e)
   {
      if(m1 != null)m1.setEbrake(true);
      if(m2 != null)m2.setEbrake(true);
      if( e )
      {
         logger.error("******* EBRAKE REQUESTED **********");
         m1.setEbrake(e);
         m2.setEbrake(e);
         execute("E");
      }
   }

   @Override
   public void init(MotorProps mp) throws Throwable
   {
      this.mp = mp;
      if( m1 != null )
      {
         try
         {
            m1.brakeAll();
         } catch(Throwable t){logger.error("COULD NOT BRAKE M1 ",t);}
      }
      if( m2 != null )
      {
         try
         {
            m2.brakeAll();
         } catch (Throwable t){logger.error("COULD NOT BRAKE M2",t);}
      }
      if( mp.get(MotorProps.IO_BACKEND_LIB).equals(MotorProps.GPIO_WIRING_PI_LIB_PROP_VAL))
      {
         m1 = new WiringPiMotorImpl("M1");
         m2 = new WiringPiMotorImpl("M2");
      }
      else if( mp.get(MotorProps.IO_BACKEND_LIB).equals(MotorProps.GPIO_PI4J_LIB_PROP_VAL))
      {
         m1 = new Pi4jMotorControl("M1");
         m2 = new Pi4jMotorControl("M2");
      }
      else if( mp.get(MotorProps.IO_BACKEND_LIB).equals(MotorProps.USB4J_LIB_PROP_VAL))
      {
         m1 = new Usb4jMotorControl("M1");
         m2 = new Usb4jMotorControl("M2");
      }
      m1.init(mp);
      m2.init(mp);
   }

   @Override
   public String dataChanged(String key, String val)
   {
      String ret = null;
      if( key.equals(MotorProps.IO_BACKEND_LIB) ||
              key.equals(MotorProps.M1_MINUS_GPIO_PIN ) ||
              key.equals(MotorProps.M1_PLUS_GPIO_PIN  ) ||
              key.equals(MotorProps.M2_MINUS_GPIO_PIN ) ||
              key.equals(MotorProps.M2_PLUS_GPIO_PIN  ) ||
              key.equals(MotorProps.SIMULATE_PI_PROP  )) {
         try {
            init(mp);
            ret = "Re-initialized data change [" + key + "] to [" + val + "]";
         } catch (Throwable t) {
            ret = "Error changing data [" + key + "] to [" + val + "] " +
                    t.getMessage();
            logger.error(ret, t);
         }
      }
      return ret;
   }
   @Override
   public String execute(String uiCmd)
   {
      String retMsg = null;
      boolean isRead = false;
      if( uiCmd.equalsIgnoreCase("E") | uiCmd.equalsIgnoreCase("ebrake")| uiCmd.equalsIgnoreCase("brake"))
      {
         m1.setEbrake(true);
         m2.setEbrake(true);
      }
      else if( uiCmd.equalsIgnoreCase("C") | uiCmd.equalsIgnoreCase("clear"))
      {
         m1.setEbrake(false);
         m2.setEbrake(false);
      }
      else {
         // get cmds that we need to run for uiCmd the  provided by caller (usually a user)
         // example, if uiCmd is "F", then we'll get something like
         // +1 for m1 and +1 for m2 which means pulse M1 (the left motor) once and
         // pulse M2 (the right motor) once
         //
         List<String> m1Cmds = (List<String>) mp.m1_cmds_to_dts.get(uiCmd);
         List<String> m2Cmds = (List<String>) mp.m2_cmds_to_dts.get(uiCmd);

         if (m1Cmds != null && m1Cmds.size() > 0) {
            m1.setExpectedNumCmds(m1Cmds.size());
            // motors get done finished
            logger.debug("Request nonBtlockingPulse(s) to M1...");
            m1.nonBlockingPulse(m1Cmds, mp.m1_cmd_run_time_ms, mp.m1_duty_cycle_hi_ms, mp.m1_duty_cycle_lo_ms);
         }
         if (m2Cmds != null && m2Cmds.size() > 0) {
            m2.setExpectedNumCmds(m2Cmds.size());
            logger.debug("Request nonBlockingPulse(s) to M2...");
            m2.nonBlockingPulse(m2Cmds, mp.m2_cmd_run_time_ms, mp.m2_duty_cycle_hi_ms, mp.m2_duty_cycle_lo_ms);
         }
         // major hacke check for read
         if( m1Cmds != null && m1Cmds.size() > 0 )
         {
            if( m1Cmds.get(0).contains("get"))
               isRead = true;
         }
         if( m2Cmds != null && m2Cmds.size() > 0 )
         {
            if( m2Cmds.get(0).contains("get"))
               isRead = true;
         }
         // if running in continuous mode, just return
         if (mp.motor_run_mode.equalsIgnoreCase(MotorProps.MOTOR_RUN_MODE_CONTINUOUS) && !isRead) {
            logger.info("Motors commanded, remember to send Ebrake to stop");
            return "Motors commanded, remember to send Ebrake to stop";
         }
         // else if running in spurt mode, then wait for the commands to finish
         if (m1Cmds != null && m1Cmds.size() > 0) {

            while (m1.getNumFinishedCommands() < m1Cmds.size() && !m1.getEbrake()) {
               try {
                  Thread.sleep(50);
               } catch (Throwable t) {
                  retMsg = "UNEXPECTED ERROR DURRING SLEEP " + t.getMessage();
                  logger.error(retMsg, t);
                  break;
               }
            }
            logger.debug("done/w M1");
            if(isRead)
            {
               logger.info("Reading...");
               String x =((Usb4jMotorControl)m1).read(20,5000);
               logger.info("read from motor1: [" + x +"]");
               retMsg = retMsg==null?x:(retMsg+" "+x);

            }
            else {
               m1.brakeAll();
            }
         }
         if (m2Cmds != null && m2Cmds.size() > 0) {

            while (m2.getNumFinishedCommands() < m2Cmds.size() && !m2.getEbrake()) {
               try {
                  Thread.sleep(50);
               } catch (Throwable t) {
                  retMsg = "UNEXPECTED ERROR DURRING SLEEP " + t.getMessage();
                  logger.error(retMsg, t);
                  break;
               }
            }
            logger.debug("done/w M2");
            if(isRead)
            {
               logger.info("Reading...");
               String x = ((Usb4jMotorControl)m2).read(20,5000);
               logger.info("read from motor2: [" + x +"]");
               retMsg = retMsg==null?x:(retMsg+" "+x);

            }
            else {
               m2.brakeAll();
            }
         }
      }
      if( !isRead ) {
         m1.brakeAll();
         m2.brakeAll();
      }
      if( retMsg == null ) retMsg = "motors commanded";
      return retMsg;
      
   }
   @Override
   public void shutdown()
   {
      m1.brakeAll();
      m2.brakeAll();
   }
}

    

      
      