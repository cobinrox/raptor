package com.cobinrox.io.mover;

import com.cobinrox.io.impl.IMotor;
import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.impl.gpio.WiringPiMotorImpl;
import org.apache.log4j.Logger;

public class RCCarMoverImpl implements IMover
{
    static final Logger logger = Logger.getLogger(RCCarMoverImpl.class);

   IMotor m1;
   IMotor m2;

    
   //HashMap<String,List< List<String>> uiToMotorCmds;
   public void shutdown(){}
    public void setEbrake(boolean e)
    {
        if(m1 != null)m1.setEbrake(true);
        if(m2 != null)m2.setEbrake(true);
        if( e )
        {
            logger.error("******* EBRAKE REQUESTED **********");
            m1.setEbrake(e);
            m2.setEbrake(e);
            move("E");
        }
    }
    public void init(MotorProps mp) throws Throwable
   {
      if( mp.get("MODE").equals("GPIO"))
      {
         m1 = new WiringPiMotorImpl("M1");
         m2 = new WiringPiMotorImpl("M2");
      }
      m1.init(mp);
      m2.init(mp);
      //initMotorCmds();
   }

    public String dataChanged(String key, String val)
    {
        return "";
    }

   public String move(String uiCmd)
   {
/*
      List<String> m1Cmds = uiToMotorCmds(uiCmd,"M1");
      List<String> m2Cmds = uiToMotorCmds(uiCmd,"M2");

      m1.setExpectedNumCmds(m1Cmds.length); // hack
      m2.setExpectedNumCmds(m2Cmds.length);
      for( int i=0; i < m1Cmds.size(); i++ )
      {
         String[] nxtDT = m1Cmds.get(i).split(",");
         
         String d = nxtDT [0];  // eg "+"
         String t = nxtDT [1]; // eg "m"
         
         log.debug("Sending nonBlockingPulse " + d + " to M1...");
         m1.nonBlockingPulse(d,t);
      }
      for( int i=0; i < m2Cmds.size(); i++ )
      {
         String[] nxtDT = m2Cmds.get(i).split(",");
         
         String d = nxtDT [0];  // eg "+"
         String t = nxtDT [1]; // eg "m"
         log.debug("Sending nonBlockingPulse " d + "to M2...");
         m1.nonBlockingPulse(d,t);
      }
      
      while(m1.numFinishedCmds < m1Cmds.length )
      {
         Thread.sleep(50);
      }
      while( m2.numFinishedCmds < m2Cmds.length )
      {
         Thread.sleep(50);
      }

      m1.brake();
      m2.brake();
      return("motors moved");
      */
      return "not implemented";
   }

   /**
    *
    *
    *      
   private List<String> uiToMotorCmds(String uiCmd, String motorAlias)
   {
       List<String> dTs = new ArrayList<String>();
       if( motorAlias.equals("M1")
       {
          if( uiCmd.equals("F") )
          {
             dTs.add("+,1");
          }
          else if( uiCmd.equals("B"))
          {
             dTs.add("-,1");
          }
          //else if( uiCmd.equals("L"))
          //{
          //   dTs.add("-,1);
          //}
          //else if( uiCmd.equals("R"))
          //{
          //   dTs.add("+,1");
          //}
          else if( uiCmd.equals("FL"))
          {
             
             dTs.add("+,1");
          }
          else if( uiCmd.equals("FR"))
          {
             dTs.add("+,1");
             
          }
          else if( uiCmd.equals("BL"))
          {
            
             dTs.add("-,1");
          }
          else if( uiCmd.equals("BR"))
          {
             dTs.add("-,1");
             
          }      
       }
       else if(motorAlias.equals("M2"))
       {
	  if( uiCmd.equals("F") )
          {
             dTs.add("");
          }
          else if( uiCmd.equals("B"))
          {
             dTs.add("");
          }
          //else if( uiCmd.equals("L"))
          //{
          //   dTs.add("+,1);
          //}
          //else if( uiCmd.equals("R"))
          //{
          //   dTs.add("-,1");
          //}
          else if( uiCmd.equals("FL"))
          {
             dTs.add("+,1");
             
          }
          else if( uiCmd.equals("FR"))
          {
             dTs.add("-,1");
             
          }
          else if( uiCmd.equals("BL"))
          {
             
             dTs.add("+,1");
          }
          else if( uiCmd.equals("BR"))
          {
             
             dTs.add("-,1");
          }      
       }

       return dTs;
   }
   private void initUiToMotorCmds()
   {
/* bleh good luck
       List<String> m1Cmds = new ArrayList<String>();
       List<String> m2Cmds = new ArrayList<String>();

       uiToMotorCmds = new List<List<String>>();

       //
       // parse "FL"
       //
       List<List<String>> cmds = new ArrayList<ArrayLIst<String>>();
       String mDts[] = mp.get("WHEELCHAIR.F").split(",");
       // mDts = "M1-:m" "M1+:h" "M2+:m" "M2+:h"
       int numMdt = mDts.length/3;
       for( int i=0; i< numMdt; i+=3 )
       {
          String MDT = mDts[i];

       }

       uiToMotorCmds.put("FL",cmds);
*/
          
   }

    

      
      