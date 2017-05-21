package com.cobinrox.io;
import java.io.IOException;
import java.util.Scanner;

import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.mover.DefaultMoverImpl;
import com.cobinrox.io.mover.IMover;
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
	static final String VERSION = "170521";
                                          // refactor var names
                                          // add command to increase duty cycle hi by percentage of cmd run time
                                          // add more info to pi4j pin info
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
                                          // * allow cmd line io properties file
                                          // * turn off pi4j motors on start up

    /**
     * Properties most of which are from io.properties
     */
	MotorProps mp = new MotorProps();

    /**
     * Mid level motor mover interface
     */
	IMover mover;

    /**
     * Optional file name that can be passed in on command line for properties file
     */
    String optionalIoPropsFilePath;

    /**
     * Indicates if started up by main (by human) or not (can be
     * started up as helper class by servlet)
     */
    static boolean main = false;

    /**
     * Constructor
     */
    public DoMotorCmd( ) throws IOException
    {
        // mp is created via static init

    }

    /**
     * Constructor
     * @param optionalIoPropsFilePath path to properties file, if null then
     *                                back-end MP tries to find properties
     *                                in classpath
     * @throws IOException
     */
    public DoMotorCmd(String optionalIoPropsFilePath) throws IOException
    {
        this.optionalIoPropsFilePath = optionalIoPropsFilePath;
        init(optionalIoPropsFilePath);
    }

    /**
     * Allows caller to re-read the props file, if, for example, the caller
     * had edited the props file and need new params to take effect
     */
    private void reReadProps() throws IOException
    {
        mp = new MotorProps();
        mp.clearProps();
        init(optionalIoPropsFilePath);
    }

    /**
     * Read props file values
     * @param optionalIoPropsPath path to props file, if null then MP does classpath
     *                            search for logical props file location
     * @throws IOException
     */
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
            mover = new DefaultMoverImpl();
        else
        */
        // right now we only have this class implemented
        mover = new DefaultMoverImpl();

        try
        {
            // initialize lower-level motor controller hware based on settings in properties file
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
		Scanner keyboardInput = new Scanner(System.in);
        try {
            while (!input.equalsIgnoreCase("x")) {
                System.out.println("Enter F/L/R/B/FL/FR/BL/BR/T/E/H/RRP/X --> ");
                input = keyboardInput.nextLine();
                System.out.println("You entered [" + input + "]");
                if (input.toUpperCase().equals("X")) {
                    System.out.println("buh-bye!");
                    return;
                }
                System.out.println(dmc.doThis(input));
            }
        }
        finally
        {
            keyboardInput.close();
        }
	}
	public String doThis(String command)
	{
		   command = command.toUpperCase();

           if( command.equals("REREAD_PROPS") || command.equals("RRP"))
           {
               try {
                   reReadProps();
               }
               catch(IOException e)
               {
                   String es = Utils.getStackTraceAsString(e);
                   logger.error("Reading io.properties file ",e);
                   return ("REREAD_PROPS ERROR: " + es);
               }
               return("REREAD_PROPS/Re-init complete.");
           }
           if(command.equals("E")) {
               mover.setEbrake(true);
               return "EBrake Set";
           }
           else if( command.equals("C")) {
               mover.setEbrake(false);
               return "EBrake cleared";
           }
	       else if(command.startsWith("D_"))
	    	    return(changeKeyValue(command));
           else if(command.startsWith("P_"))
                return(changePercentValue(command));
           else if( command.equals("SAV"))
               return( "Saved data to file [" + Utils.savePropsToUserDir(mp.rawProps,"io") + "]");
           else if( command.equals("DEL"))
               return(  Utils.delOldPropsFileFromUserDir("io"));
           else if(command.startsWith("RD"))
                return(readData(command.toUpperCase()));
           else if(command.equals("H"))
               return help();

           else
			    return(executeCommand(command,null));

	}

    /**
     * Read data from properties
     * @param dataStr
     * @return
     */
    protected String readData(String dataStr)
    {
        return MotorProps.getPropsAsDisplayString("\n");

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
	protected String changeKeyValue(String dataStr)
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

    /**
     * Change the value of the duty hi/lo values for a motor as a percentage of the total command run time.
     * Example: run time cycle: 1 sec
     * To change m1's motor to be 50% hi, use P_M1_50
     * @param dataStr Percentage command in the format P_<ALIAS>_<PERCENTAGE>, e.g. P_M1_50
     * @return
     */
    protected String changePercentValue(String dataStr)
    {
        String ret = "done";
        System.out.println("percent change request [" + dataStr + "]");

        String[] parsedVals = dataStr.split("_");
        if( parsedVals.length < 3 )
        {
            ret = "Invalid data percent request [" + dataStr + "] Expecting\n";
            ret+= "P_M1_<percentVal>\n"+
                  "P_M2_<percentVal>";
            return ret;
        }
        String key     = parsedVals[1];
        String percent = parsedVals[2];
        int percentI = 0;
        try
        {
            percentI = Integer.parseInt(percent);
        }
        catch(Throwable t)
        {
            ret = "Invalid percent value [" + percent + "]";
            return ret;
        }
        try
        {
             MotorProps.setDutyHiAsPercentOfTotalRunTime(key,percentI);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            ret = "EXCEPTION (" + t.getMessage() + ") DURING DATA CHANGE REQUEST [" + dataStr + "] Expecting one of\n";
            ret+= help();
            return ret;
        }
        //mover.dataChanged(key, newVal);
        return ret;
    }

    /**
     * Display commands
     * @return String containing commands
     */
    public static String help()
    {
       return help(main?false:true);
    }

    /**
     * Display commands with optional html BR rather than \n as line delimiter
     * @param html boolean, true=yes include <br/> at end of lines, false=no, just use \n
     * @return list of commands
     */
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
        sb.append("P_MX_<integer> (set MX duty hi as % of run time)" + (html?"<br/>":"\n"));
		sb.append("SAV (save props file)" + (html?"<br/>":"\n"));
        sb.append("DEL (del old props file)" + (html?"<br/>":"\n"));
        sb.append("RD (read/dump current prop vals)" + (html?"<br/>":"\n"));
        sb.append("RRP/REREAD_PROPS (re-initialize from props files)" + (html?"<br/>":"\n"));
        sb.append("F/L/R/B/FL/FR/BL/BR/H/X (Fwd/Lft/Rt/Bck/Help/eXit)" + (html?"<br/>":"\n"));
		return sb.toString();
	}
	protected String executeCommand(String uiCmd, String notUsed)
	{
		String ret = null;
		try
		{
			ret = mover.execute(uiCmd);
		}
		catch(Throwable t)
		{
			ret = "ERROR DURING EXECUTION " + Utils.getStackTraceAsString(t);
			logger.error("ERROR DURING EXECUTION",t);
		}
		return ret;
	}

	public void shutdown() throws Throwable
	{
        mover.setEbrake(true);
		mover.shutdown();
	}

}
