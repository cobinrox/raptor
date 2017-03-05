package com.cobinrox.io;
import java.io.IOException;
import java.util.Scanner;

import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.mover.IMover;
import com.cobinrox.io.mover.WheelChairMoverImpl;
import org.apache.log4j.Logger;

import com.cobinrox.common.Utils;

/**
 *  High level class to send commands down to lower-level classes.
 *  Can be used interactively with a user to get user ad-hoc commands-- use the main method for this mode.
 *  Can be called from a web servlet to execute ad-hoc commands called from the servlet.
 *  If running within IntelliJ, be sure to add the following to the run configuration's VM param settings
 *  for log4j to work:
 *  -Dlog4j.configuration=file:C:\rrr\raptor\io\target\log4j.xml
 *
 *  If running out of IDE, be sure to copy io.properties and log4j.xml files to io/target/classes
 *
 *  If running on PI, must be run as root (or sudo)
 */
public class DoMotorCmd {
	static final Logger logger = Logger.getLogger(DoMotorCmd.class);
	static final String VERSION = "170224";
                                          // add debug statement to Pi4JMotorControl
                                          // add read capability
                                          // fix non motor command execution logic, add ebrake call
                                          // provide ability to send non motor commands to saber, e.g. R:512
                                          // more sabertooth logic
                                          // account for sabertooth logic
                                          // *major hack for chinese i/o card enable/disable logic
                                          // *do a brake after  individual motor control threads return,
                                          // should allow, for example M1 to run only 2 sec, but M2 to run
                                          // 5 sec w/o M1 continuing to run the whole 5 secs
                                          // *return all motor props as string on a RD command
                                          // *allow re-read of props file on the fly, accept optional
                                          // io.propss file as args to program
                                          // * extend alias string to DTS commands so that individual
                                          // cars can have mmaintainable config values & sort props before storing
                                          // * change pulse loop to stop after cmd runtime rather than calculating
                                          // number of pulses
                                          // add output when using Enable/Direction pin
                                          // more outpt for bad io.props file

    /**
     * Properties most of which are from io.properties
     */
	MotorProps mp = new MotorProps();

    /**
     * Low level motor mover interface
     */
	IMover mover;

    /**
     * Optional file name that can be passed in on command line
     */
    String optionalIoPropsFilePath;

    /**
     * Indicates if started up by main (by human) or not (maybe
     * started up as helper class by servlet)
     */
    static boolean main = false;

    /**
     * Constructor
     */
    public DoMotorCmd( ) throws IOException
    {
        // mp is created via static init

        // this init will set mp variables, types etc
        init(null);
    }
    public DoMotorCmd(String optionalIoPropsFilePath) throws IOException
    {
        this.optionalIoPropsFilePath = optionalIoPropsFilePath;
        init(optionalIoPropsFilePath);
    }

    /**
     * Allows caller to re-read the props file
     */
    private void reReadProps() throws IOException
    {
        mp = new MotorProps();
        mp.clearProps();
        init(optionalIoPropsFilePath);
    }
    private void init(String optionalIoPropsPath) throws IOException
    {
        mp.setMotorVariables(optionalIoPropsPath,true);
        if( mp.simulate_pi)
        {
            logger.info("***************************");
            logger.info("     W A R N I N G   ! !  *");
            logger.info("                          *");
            logger.info(" Simulating HARDWARE  !!  *");
            logger.info("                          *");
            logger.info("***************************");
        }
        /*
        if( mp.mover_config.equals(MotorProps.MOTOR_CONFIG_PROP_VAL_REMOTE_CAR))
            mover = new RCCarMoverImpl() ;
            //else if( mp.mover_config.equals(MotorProps.MOTOR_CONFIG_PROP_VAL_SINGLE))
            //	mover = new SimpleSingleMotorControl();
        else if( mp.mover_config.equals(MotorProps.MOTOR_CONFIG_PROP_VAL_WHEEL_CHAIR))
            mover = new WheelChairMoverImpl();
        else
        */
        // right now we only have this classs implemented, but despite its name, it is
        // generic, i.e. NOT tied to wheelchair type implementation
        mover = new WheelChairMoverImpl();

        try
        {
            mover.init(mp);
        }
        catch(Throwable t)
        {
            logger.error("COULD NOT INIT HWARE",t);
            return;
        }
    }

    /**
     * Main method to use with a human
     *
     * @param args not used
     */
	public static void main(String args[]) throws IOException
    {
        main = true;
		System.out.println("* * * * * * * VERSION " + VERSION + " * * * * * * * *");
        DoMotorCmd dmc = null;
        if (args != null & args.length > 0)
		    dmc= new DoMotorCmd( args[0]);
        else
            dmc = new DoMotorCmd();
        if( dmc.mp.simulate_pi)
        {
            System.out.println("***************************");
            System.out.println("     W A R N I N G   ! !  *");
            System.out.println("                          *");
            System.out.println(" Simulating IO  !!        *");
            System.out.println("                          *");
            System.out.println("***************************");
        }

        System.out.println("* * * * * * * VERSION " + VERSION + " * * * * * * * *");
        System.out.println(" Greetings, you can enter the following commands:");
        System.out.println(dmc.help(false));
		String input = "";
		Scanner scanIn = new Scanner(System.in);
        try {
            while (!input.equalsIgnoreCase("x")) {
                System.out.println("Enter F/L/R/B/FL/FR/BL/BR/T/E/H/X --> ");
                input = scanIn.nextLine();
                System.out.println("You entered [" + input + "]");
                if (input.toUpperCase().equals("X")) {
                    System.out.println("buh-bye!");
                    return;
                }
                //defaults to forward, but nah if( input.length() == 0) input="F";
                System.out.println(dmc.doThis(input));
            }
        }
        finally
        {
            scanIn.close();
        }
	}
	public String doThis(String fblr )
	{
		   fblr = fblr.toUpperCase();

           if( fblr.equals("REREAD_PROPS"))
           {
               try {
                   reReadProps();
               }
               catch(IOException e)
               {
                   logger.error("Reading io.properties file ",e);
               }
               return("Re-init complete.");
           }
           if(fblr.equals("E")) {
               mover.setEbrake(true);
               return "EBrake Set";
           }
           else if( fblr.equals("C")) {
               mover.setEbrake(false);
               return "EBrake cleared";
           }
	       else if(fblr.startsWith("D_"))
	    	    return(changeData(fblr.toUpperCase()));
           else if( fblr.equals("SAV"))
               return( "Saved data to file [" + Utils.savePropsToUserDir(mp.rawProps,"io") + "]");
           else if( fblr.equals("DEL"))
               return(  Utils.delOldPropsFileFromUserDir("io"));
           else if(fblr.startsWith("RD"))
                return(readData(fblr.toUpperCase()));
           else if(fblr.equals("H"))
               return help();

           else
			    return(moveOneCmdCycle(fblr,null));

	}

    protected String readData(String dataStr)
    {
        return MotorProps.getPropsAsDisplayString("\n");
        /*
        String ret = "";
        System.out.println("read request [" + dataStr + "]");

        String[] parsed = dataStr.split("_");
        if( parsed.length < 2 )
        {
            ret = "Invalid data read request [" + dataStr + "] Expecting R of\n";
            ret+= help();
            return ret;
        }
        String key = parsed[1];

        try
        {
            if( key.equals("M1RT"))
            {
                ret = dataStr + " = [" + mp.get(mp.M1_CMD_RUN_TIME_MS_PROP) +"]";
            }
            else if( key.equals("M2RT"))
            {
                ret = dataStr + " = [" + mp.get(mp.M2_CMD_RUN_TIME_MS_PROP) +"]";
            }
            else if(key.equals("M1HI"))
            {
                ret = dataStr + " = [" + mp.get(mp.M1_DUTY_CYCLE_HI_MS_PROP) +"]";
            }
            else if(key.equals("M1LO"))
            {
                ret = dataStr + " = [" + mp.get(mp.M1_DUTY_CYCLE_LO_MS_PROP) +"]";
            }
            else if(key.equals("M2HI"))
            {
                ret = dataStr + " = [" + mp.get(mp.M2_DUTY_CYCLE_HI_MS_PROP) +"]";
            }
            else if(key.equals("M2LO"))
            {
                ret = dataStr + " = [" + mp.get(mp.M2_DUTY_CYCLE_LO_MS_PROP) +"]";
            }
            else if(key.equals("M1+"))
            {
                ret = dataStr + " = [" + mp.get(mp.M1_PLUS_GPIO_PIN) +"]";
            }
            else if(key.equals("M1-"))
            {
                ret = dataStr + " = [" + mp.get(mp.M1_MINUS_GPIO_PIN) +"]";
            }
            else if(key.equals("M2+"))
            {
                ret = dataStr + " = [" + mp.get(mp.M2_PLUS_GPIO_PIN) +"]";
            }
            else if(key.equals("M2-"))
            {
                ret = dataStr + " = [" + mp.get(mp.M2_MINUS_GPIO_PIN) +"]";
            }
            else if(key.equals("SIM"))
            {
                ret = dataStr + " = [" + mp.get(mp.SIMULATE_PI_PROP) +"]";
            }
        }
        catch(Throwable t)
        {
            ret = "Invalid data read request [" + dataStr + "] Expecting one of\n";
            ret+= help();

            return ret;
        }
        return ret;
        */
    }
    /**
     * Change back-end motor properties.
     *
     * @param dataStr must be in the format of:
     *                D_KEY_VALUE
     *                where D = data command itself
     *                KEY = property to change (see help)
     *                VALUE = new value to apply to key
     *                underscores are used to separate the three parts
     * @return
     */
	protected String changeData(String dataStr)
    {
        String ret = "done";
        System.out.println("data change request [" + dataStr + "]");

        String[] parsed = dataStr.split("_");
        if( parsed.length < 3 )
        {
            ret = "Invalid data change request [" + dataStr + "] Expecting one of\n";
            ret+= help();

            return ret;
        }
        String key = dataStr.substring(2,dataStr.lastIndexOf("_"));
        String newVal = parsed[parsed.length-1];

        //sb.append("D_RT_<float> (set cmd run time ms)");
        //sb.append("D_HI_<float> (set duty hi time ms)");
        //sb.append("D_LO_<float> (set duty lo time ms)");
        try
        {
            if( key.equals("M1RT"))
            {
                mp.set(mp.M1_CMD_RUN_TIME_MS_PROP,newVal);
                ret = "Updated " + mp.M1_CMD_RUN_TIME_MS_PROP + " [" + newVal + "]";
            }
            else if(key.equals("M1HI"))
            {
                mp.set(mp.M1_DUTY_CYCLE_HI_MS_PROP,newVal);
                ret = "Updated " + mp.M1_DUTY_CYCLE_HI_MS_PROP + " [" + newVal + "]";
            }
            else if(key.equals("M1LO"))
            {
                mp.set(mp.M1_DUTY_CYCLE_LO_MS_PROP,newVal);
                ret = "Updated " + mp.M1_DUTY_CYCLE_LO_MS_PROP + " [" + newVal + "]";
            }
            else if( key.equals("M2RT"))
            {
                mp.set(mp.M2_CMD_RUN_TIME_MS_PROP,newVal);
                ret = "Updated " + mp.M2_CMD_RUN_TIME_MS_PROP + " [" + newVal + "]";
            }
            else if(key.equals("M2HI"))
            {
                mp.set(mp.M2_DUTY_CYCLE_HI_MS_PROP,newVal);
                ret = "Updated " + mp.M2_DUTY_CYCLE_HI_MS_PROP + " [" + newVal + "]";
            }
            else if(key.equals("M2LO"))
            {
                mp.set(mp.M2_DUTY_CYCLE_LO_MS_PROP,newVal);
                ret = "Updated " + mp.M2_DUTY_CYCLE_LO_MS_PROP + " [" + newVal + "]";
            }
            else if(key.equals("M1+"))
            {
                mp.set(mp.M1_PLUS_GPIO_PIN,newVal);
                mover.dataChanged(mp.M1_PLUS_GPIO_PIN,newVal);
                ret = "Updated " + mp.M1_PLUS_GPIO_PIN + " [" + newVal + "]";
            }
            else if(key.equals("M1-"))
            {
                mp.set(mp.M1_MINUS_GPIO_PIN,newVal);
                mover.dataChanged(mp.M1_MINUS_GPIO_PIN, newVal);

                ret = "Updated " + mp.M1_MINUS_GPIO_PIN + " [" + newVal + "]";
            }
            else if(key.equals("M2+"))
            {
                mp.set(mp.M2_PLUS_GPIO_PIN,newVal);
                mover.dataChanged(mp.M2_PLUS_GPIO_PIN, newVal);

                ret = "Updated " + mp.M2_PLUS_GPIO_PIN + " [" + newVal + "]";
            }
            else if(key.equals("M2-"))
            {
                mp.set(mp.M2_MINUS_GPIO_PIN,newVal);
                mover.dataChanged(mp.M2_PLUS_GPIO_PIN, newVal);

                ret = "Updated " + mp.M2_MINUS_GPIO_PIN + " [" + newVal + "]";
            }
            else if(key.equals("SIM"))
            {
                newVal= newVal.equals("1")?"true":"false";
                mp.set(mp.SIMULATE_PI_PROP,newVal);
                mover.dataChanged(mp.SIMULATE_PI_PROP, newVal);

                ret = "Updated " + mp.SIMULATE_PI_PROP + " [" + newVal + "]";
            }
            else if(key.equals("LIB"))
            {
                if( newVal.equals("W")) newVal=MotorProps.GPIO_WIRING_PI_LIB_PROP_VAL;
                if( newVal.equals("P")) newVal=MotorProps.GPIO_PI4J_LIB_PROP_VAL;
                if( newVal.equals("U")) newVal=MotorProps.USB4J_LIB_PROP_VAL;

                mp.set(mp.IO_BACKEND_LIB,newVal );
                mover.dataChanged(mp.IO_BACKEND_LIB, newVal);

                ret = "Updated " + mp.IO_BACKEND_LIB + " [" + newVal + "]";
            }
            else if(key.equals("M1F"))
            {
                mp.addCommandAndSubCommands(mp.m1_cmds_to_dts,key.substring(2),newVal);


            }
            else if(key.equals("M2F"))
            {
                mp.addCommandAndSubCommands(mp.m2_cmds_to_dts,key.substring(2),newVal);

            }
            else if(key.equals("AVG") || key.equals(mp.USB_VOLT_STRENGTH_PROP))
            {
                mp.set(mp.USB_VOLT_STRENGTH_PROP,newVal);
                mp.usb_USB_VOLT_STRENGTH = Integer.parseInt(newVal);
            }
            else
            {
                logger.info("Changing [" + key + "] from [" + mp.get(key) + "] to [" + newVal.toUpperCase() + "]");
                mp.set(key,newVal.toUpperCase());
                //ret = "INVALID DATA CHANGE REQUEST [" + dataStr + "] EXPECTING ONE OF\n";
                //ret+= help();
                //return ret;
            }
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            ret = "EXCEPTION (" + t.getMessage() + ") DURING DATA CHANGE REQUEST [" + dataStr + "] Expecting one of\n";
            ret+= help();
            return ret;
        }
        mover.dataChanged(key, newVal);
        return ret;
    }
    public static String help()
    {
       return help(main?false:true);
    }

	public static String help(boolean html)
	{
		StringBuffer sb = new StringBuffer("");
		sb.append("D_M1+_<integer> (set M1+ GPIO )" + (html?"<br/>":"\n"));
		sb.append("D_M1-_<integer> (set M1- GPIO )" + (html?"<br/>":"\n"));
		sb.append("D_M2+_<integer> (set M2+ GPIO )" + (html?"<br/>":"\n"));
		sb.append("D_M2-_<integer> (set M2- GPIO )" + (html?"<br/>":"\n"));
		sb.append("D_M1RT_<float> (set M1 cmd run time ms)" + (html?"<br/>":"\n"));
		sb.append("D_M1HI_<float> (set M1 duty hi time ms)" + (html?"<br/>":"\n"));
		sb.append("D_M1LO_<float> (set M1 duty lo time ms)" + (html?"<br/>":"\n"));
        sb.append("D_M2RT_<float> (set M2 cmd run time ms)" + (html?"<br/>":"\n"));
        sb.append("D_M2HI_<float> (set M2 duty hi time ms)" + (html?"<br/>":"\n"));
        sb.append("D_M2LO_<float> (set M2 duty lo time ms)" + (html?"<br/>":"\n"));
        sb.append("D_LIB_W (change to wiringPi lib)" + (html?"<br/>":"\n"));
        sb.append("D_LIB_P (change to pi4j lib)" + (html?"<br/>":"\n"));
        sb.append("D_LIB_U (change to usb4j lib)" + (html?"<br/>":"\n"));
        sb.append("D_MOTOR_RUN_MODE_<spurt or continuous>" + (html?"<br/>":"\n"));
        sb.append("D_AVG_<integer> (set USB voltage val)" + (html?"<br/>":"\n"));
		sb.append("D_SIM_<integer> (set simulate mode)" + (html?"<br/>":"\n"));
		sb.append("SAV (save props file)" + (html?"<br/>":"\n"));
        sb.append("DEL (del old props file)" + (html?"<br/>":"\n"));
        sb.append("RD (read/dump current prop vals)" + (html?"<br/>":"\n"));
        sb.append("REREAD_PROPS (re-initialize from props files)" + (html?"<br/>":"\n"));
        sb.append("F/L/R/B/FL/FR/BL/BR/H/X (Fwd/Lft/Rt/Bck/Help/eXit)" + (html?"<br/>":"\n"));
		return sb.toString();
	}
	protected String moveOneCmdCycle(String uiCmd, String notUsed)
	{
		String ret = null;
		try
		{
			ret = mover.move(uiCmd);
		}
		catch(Throwable t)
		{
			ret = "ERROR DURING MOVE " + Utils.getStackTraceAsString(t);
			logger.error("ERROR DURING MOVE",t);
		}
		return ret;
	}

	public void shutdown() throws Throwable
	{
        mover.setEbrake(true);
		mover.shutdown();
	}

}
