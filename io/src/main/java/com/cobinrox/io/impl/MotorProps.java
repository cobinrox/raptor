package com.cobinrox.io.impl;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;

import com.cobinrox.common.Utils;

public class MotorProps {
	static final Logger logger = Logger.getLogger(MotorProps.class);
    public final static String REGX_split_on_commas_but_not_in_single_quotes =",(?=(?:[^\']*\'[^\']*\')*[^\']*$)";
    public final static String REGX_split_on_colons_but_not_in_single_quotes =":(?=(?:[^\']*\'[^\']*\')*[^\']*$)";
	public final static String FORWARD  = "forward";
	public final static String BACKWARD = "backward";
	public final static String LEFT     = "left";
	public final static String RIGHT    = "right";
	
	public static int    m1_PLUS_GPIO_PIN; public static final String M1_PLUS_GPIO_PIN  = "M1_PLUS_GPIO_PIN";
	public static int    m1_MINUS_GPIO_PIN;public static final String M1_MINUS_GPIO_PIN = "M1_MINUS_GPIO_PIN";
	public static int    m2_MINUS_GPIO_PIN;public static final String M2_MINUS_GPIO_PIN = "M2_MINUS_GPIO_PIN";
	public static int    m2_PLUS_GPIO_PIN; public static final String M2_PLUS_GPIO_PIN  = "M2_PLUS_GPIO_PIN";
    public static short  usb_ID_VENDOR;    public static final String USB_ID_VENDOR     = "USB_ID_VENDOR";
    public static short  usb_ID_PRODUCT;   public static final String USB_ID_PRODUCT    = "USB_ID_PRODUCT";

    /**
     * For saber board, this value represents the value that we normally send to the motor to move, it can
     * be a value from 0 to 2047, where the lower the absolute value, the less voltage and strength
     * is applied to the motors, so to give the motor more oopmph, you could set this value to a higher
     * number.  This number is used to move either forward or backward, although going backward on wheelchair
     * sometimes needs more oomph, so we might want to provide both a forward and backward volt strength
     * value.
     * This value, by the way, is similar to the pulse mode params that are used for the GPIO motor control, it's
     * just that the saber board is much easier to control
     */
    public static int    usb_USB_VOLT_STRENGTH;    public static final String USB_VOLT_STRENGTH_PROP = "USB_VOLT_STRENGTH";

	public static int    m1_cmd_run_time_ms; public static final String M1_CMD_RUN_TIME_MS_PROP  = "M1_CMD_RUN_TIME_MS";
	public static int    m2_cmd_run_time_ms; public static final String M2_CMD_RUN_TIME_MS_PROP  = "M2_CMD_RUN_TIME_MS";
	public static int    gpio_on;      public static final String GPIO_ON_STR_PROP      = "GPIO_ON_STR";
	public static int    gpio_off;     public static final String GPIO_OFF_STR_PROP     = "GPIO_OFF_STR";
	public static String backendLib;   public static final String IO_BACKEND_LIB        = "BACKEND_LIBRARY";
	public static int    num_motors;   public static final String NUM_MOTORS_PROP       = "NUM_MOTORS";
	public static String mover_config; public static final String MOVER_CONFIG_PROP     = "MOVER_CONFIG";
    public static String motor_run_mode; public static final String MOTOR_RUN_MODE_PROP = "MOTOR_RUN_MODE";
    public final static String MOTOR_RUN_MODE_SPURT = "spurt";
    public final static String MOTOR_RUN_MODE_CONTINUOUS="continuous";
	
	public static final String GPIO_PI4J_LIB_PROP_VAL      = "pi4j";
	public static final String GPIO_WIRING_PI_LIB_PROP_VAL = "wiring_pi";
	public static final String USB4J_LIB_PROP_VAL          = "usb4j";

    /* dont need, can be created in props file
	public static final String MOTOR_CONFIG_PROP_VAL_SINGLE ="simple_single";
	public static final String MOTOR_CONFIG_PROP_VAL_REMOTE_CAR ="remote_car";
	public static final String MOTOR_CONFIG_PROP_VAL_WHEEL_CHAIR ="wheel_chair";
	*/
	
	
	public static int m1_duty_cycle_hi_ms;public static final String M1_DUTY_CYCLE_HI_MS_PROP = "M1_DUTY_CYCLE_HI_MS";
	public static int m1_duty_cycle_lo_ms;public static final String M1_DUTY_CYCLE_LO_MS_PROP = "M1_DUTY_CYCLE_LO_MS";

    public static int m2_duty_cycle_hi_ms;public static final String M2_DUTY_CYCLE_HI_MS_PROP = "M2_DUTY_CYCLE_HI_MS";
    public static int m2_duty_cycle_lo_ms;public static final String M2_DUTY_CYCLE_LO_MS_PROP = "M2_DUTY_CYCLE_LO_MS";

    //public int PERIOD_MS             ;public static final String PERIOD_MS_PROP = "PERIOD_MS";

    public static HashMap m1_cmds_to_dts; public static final String M1_CMDS_TO_DTS_PROP = "M1_CMDS_TO_DTS";
    public static HashMap m2_cmds_to_dts; public static final String M2_CMDS_TO_DTS_PROP = "M2_CMDS_TO_DTS";

	public static boolean simulate_pi;public static final String SIMULATE_PI_PROP = "SIMULATE_PI";

    public static String dummy;public static final String DUMMY_PROP = "DUMMY";

	public static Properties rawProps;
    static String optionalPropsPath;

    static MotorProps instance = new MotorProps();
    public void clearProps()
    {
        rawProps = null;
    }
    public String getPropsFileName()
    {
        return Utils.determinePropsFile(optionalPropsPath,instance,"io");
    }
	public static  void setMotorVariables(String optionalIoPropsFilePath, boolean verbose) throws IOException
	{
        logger.info("optionalIoPropsFilePath: [" + optionalIoPropsFilePath + "]");
        optionalPropsPath = optionalIoPropsFilePath;
		if( rawProps == null ) {
            logger.info("Reading props file...");
            rawProps = Utils.readProps(optionalIoPropsFilePath, instance, "io");
            if( rawProps == null || rawProps.size() == 0)
            {
                System.err.println("Could not parse/find io.props file");
                logger.error("Could not parse/find io.props file");
                throw new IOException("Could not parse/find io.properties file");
            }

        }
		try
		{
            simulate_pi = Boolean.parseBoolean(rawProps.getProperty(SIMULATE_PI_PROP).trim());
            if(verbose)logger.info("Simulate PI: [" + simulate_pi + "]");
		
            backendLib = rawProps.getProperty(IO_BACKEND_LIB).trim();
            if(verbose)logger.info("Use hw lib: [" + backendLib + "]");

            num_motors = Integer.parseInt(rawProps.getProperty(NUM_MOTORS_PROP).trim());
            if(verbose)logger.info("Num motors: [" + num_motors + "]");

            mover_config = rawProps.getProperty(MOVER_CONFIG_PROP).trim();
            if(verbose)logger.info("Motor config: [" + mover_config + "]");

            motor_run_mode = rawProps.getProperty(MOTOR_RUN_MODE_PROP).trim().toUpperCase();
            if(verbose)logger.info("Motor Run Mode: [" + motor_run_mode + "]");

            if(!backendLib.equals(USB4J_LIB_PROP_VAL)) {
                m1_PLUS_GPIO_PIN = Integer.parseInt(rawProps.getProperty(mover_config + "_" + M1_PLUS_GPIO_PIN).trim());
                m1_MINUS_GPIO_PIN = Integer.parseInt(rawProps.getProperty(mover_config + "_" + M1_MINUS_GPIO_PIN).trim());
                m2_MINUS_GPIO_PIN = Integer.parseInt(rawProps.getProperty(mover_config + "_" + M2_MINUS_GPIO_PIN).trim());
                m2_PLUS_GPIO_PIN = Integer.parseInt(rawProps.getProperty(mover_config + "_" + M2_PLUS_GPIO_PIN).trim());
                if (verbose )
                    logger.info("Pins to use: (f/b/l/r) [" + m1_PLUS_GPIO_PIN + "/" +
                            m1_MINUS_GPIO_PIN + "/" +
                            m2_PLUS_GPIO_PIN + "/" +
                            m2_MINUS_GPIO_PIN + "]");
            }
            else
            {
                usb_ID_PRODUCT = Short.parseShort(rawProps.getProperty(USB_ID_PRODUCT).trim().toLowerCase().replace("0x",""),16);
                usb_ID_VENDOR  = Short.parseShort(rawProps.getProperty(USB_ID_VENDOR ).trim().toLowerCase().replace("0x",""),16);
                usb_USB_VOLT_STRENGTH  = Integer.parseInt(rawProps.getProperty(USB_VOLT_STRENGTH_PROP).trim());
            }
            m1_duty_cycle_hi_ms = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M1_DUTY_CYCLE_HI_MS_PROP).trim());
            m1_duty_cycle_lo_ms = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M1_DUTY_CYCLE_LO_MS_PROP).trim());
            m1_cmd_run_time_ms  = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M1_CMD_RUN_TIME_MS_PROP).trim());
            if(verbose)logger.info("M1 Hi Duty Cycle: [" + m1_duty_cycle_hi_ms + "]");
            if(verbose)logger.info("M1 Lo Duty Cycle: [" + m1_duty_cycle_lo_ms + "]");
            if(verbose)logger.info("M1 Cmd time: [" + m1_cmd_run_time_ms + "]");

            m2_duty_cycle_hi_ms = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M2_DUTY_CYCLE_HI_MS_PROP).trim());
            m2_duty_cycle_lo_ms = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M2_DUTY_CYCLE_LO_MS_PROP).trim());
            m2_cmd_run_time_ms = Integer.parseInt(rawProps.getProperty(mover_config+"_"+M2_CMD_RUN_TIME_MS_PROP).trim());
            if(verbose)logger.info("M2 Hi Duty Cycle: [" + m2_duty_cycle_hi_ms + "]");
            if(verbose)logger.info("M2 Lo Duty Cycle: [" + m2_duty_cycle_lo_ms + "]");
            if(verbose)logger.info("M2 Cmd time: [" + m2_cmd_run_time_ms + "]");

            if(!backendLib.equals(USB4J_LIB_PROP_VAL)) {
                gpio_on = Integer.parseInt(rawProps.getProperty(GPIO_ON_STR_PROP).trim());
                gpio_off = Integer.parseInt(rawProps.getProperty(GPIO_OFF_STR_PROP).trim());
            }

            m1_cmds_to_dts = parseHighLevelMotorCmdsToLowLevelDTs(rawProps.getProperty(mover_config + "_" + M1_CMDS_TO_DTS_PROP).trim(),false) ;
            m2_cmds_to_dts = parseHighLevelMotorCmdsToLowLevelDTs(rawProps.getProperty(mover_config + "_" + M2_CMDS_TO_DTS_PROP).trim(),false) ;
            if( verbose )
            {
                logger.info("M1 CMDS to DTs:");
                for( String cmd:(Set<String>)m1_cmds_to_dts.keySet())
                {
                    String v = "";
                    List<String> dTs = (List<String>) m1_cmds_to_dts.get(cmd);
                    for( String dT:dTs)
                    {
                        v += dT + " ";
                    }
                    logger.info(cmd + ": " + v);
                }
                logger.info("M2 CMDS to DTs:");
                for( String cmd:(Set<String>)m2_cmds_to_dts.keySet())
                {
                    String v = "";
                    List<String> dTs = (List<String>) m2_cmds_to_dts.get(cmd);
                    for( String dT:dTs)
                    {
                        v += dT + " ";
                    }
                    logger.info(cmd + ": " + v);
                }
            }
            dummy = rawProps.getProperty(DUMMY_PROP);
            if(verbose) logger.info("DUMMY: [" + dummy + "]");
		}
		catch(Throwable t)
		{
			logger.error("Reading or setting properties",t);
		}
	}
    public static String getPropsAsDisplayString(String eol)
    {
        StringBuffer sb = new StringBuffer("");
        sb.append("IO props file: [" + optionalPropsPath + "]");
        sb.append("Actual props file: [" + Utils.determinePropsFile(optionalPropsPath,instance,"io")+"]");
        sb.append("Dummy: [" + dummy + "]");
        sb.append("Simulate PI: [" + simulate_pi + "]");
        sb.append(eol);

        sb.append("Use hw lib: [" + backendLib + "]");
        sb.append(eol);

        sb.append("Num motors: [" + num_motors + "]");
        sb.append(eol);

        sb.append("Motor run mode: [" + motor_run_mode + "]");
        sb.append(eol);

        sb.append("Motor config: [" + mover_config + "]");
        sb.append(eol);

        if(!backendLib.equals(USB4J_LIB_PROP_VAL))
        {
            sb.append("Pins to use: (f/b/l/r) [" + m1_PLUS_GPIO_PIN + "/" +
                    m1_MINUS_GPIO_PIN + "/" +
                    m2_PLUS_GPIO_PIN + "/" +
                    m2_MINUS_GPIO_PIN + "]");
            sb.append(eol);
        }
        else
        {
            sb.append("USB Product/Vendor: [" + usb_ID_PRODUCT + "/" + usb_ID_VENDOR + "]");
            sb.append("USB Volt Strength Val: [" + USB_VOLT_STRENGTH_PROP + "]");
            sb.append(eol);
        }

        sb.append("M1 Hi Duty Cycle: [" + m1_duty_cycle_hi_ms + "]");sb.append(eol);
        sb.append("M1 Lo Duty Cycle: [" + m1_duty_cycle_lo_ms + "]");sb.append(eol);
        sb.append("M1 Cmd time: [" + m1_cmd_run_time_ms + "]");sb.append(eol);

        sb.append("M2 Hi Duty Cycle: [" + m2_duty_cycle_hi_ms + "]");sb.append(eol);
        sb.append("M2 Lo Duty Cycle: [" + m2_duty_cycle_lo_ms + "]");sb.append(eol);
        sb.append("M2 Cmd time: [" + m2_cmd_run_time_ms + "]");sb.append(eol);
        {
            sb.append("M1 CMDS to DTs:");sb.append(eol);
            for( String cmd:(Set<String>)m1_cmds_to_dts.keySet())
            {
                String v = "";
                List<String> dTs = (List<String>) m1_cmds_to_dts.get(cmd);
                for( String dT:dTs)
                {
                    v += dT + " ";
                }
                sb.append(cmd + ": " + v);sb.append(eol);
            }
            sb.append("M2 CMDS to DTs:");sb.append(eol);
            for( String cmd:(Set<String>)m2_cmds_to_dts.keySet())
            {
                String v = "";
                List<String> dTs = (List<String>) m2_cmds_to_dts.get(cmd);
                for( String dT:dTs)
                {
                    v += dT + " ";
                }
                sb.append(cmd + ": " + v);sb.append(eol);
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param mcmdsPropVal formatted like this:
     * "F:+1,B:-1,L:+1,R:-1,FL:+1|+1,FR:-1|+1,BL:-1|-1,BR:+1|-1,T:+1|-1,'RAMP:'M1:FUN'"
     *  Which in the io.properties file would look like this:
     *  wheel_chair_M1_CMDS_TO_DTS=F:+1,B:-1,L:-1,R:+1,FL:-1|+1,FR:+1|+1,BL:+1|-1,BR:-1|-1,T:+1|-1,RAMP:'M1:FUN'
     *  which means or translates into something like:
     *  For a FORWARD (F) movement, pulse once to M1+ (+1)
     *  For a BACKWARD (B) movement, pulse once to M1- (-1)
     *  For a LEFT (L) movement, pulse once to M1-
     *  For a Forward Left (FL) movement, pulse once to M1- and once to M1+
     *  For a Forward Right (FR) movement, pulse once to M1+ and then another to M1+
     *
     * @return List of cmds to direction/times something like this:
     * [0]
     *
     */
    public static HashMap parseHighLevelMotorCmdsToLowLevelDTs(String mcmdsPropVal, boolean json) throws Throwable
    {
        String[] mcmdsArray = mcmdsPropVal.split(REGX_split_on_commas_but_not_in_single_quotes);
        HashMap cmdToDts = new HashMap();
        for(int i=0; i < mcmdsArray.length; i++ )
        {
            // dealing with something like this
            // F:+1
            // FR:-1|+1
            // so first get the name of the command string, e.g. "F", or "BACKWARD", or "T"
            // or "RAMP" or "R", etc
            //String cmd = mcmdsArray[i].split("\\:")[0];
            String cmd = mcmdsArray[i].split(REGX_split_on_colons_but_not_in_single_quotes)[0];
            // then get the list of pulses for this command, e.g. "+1" or "'MD:0'" or "'MD:0'|'MT:-512'"
            //addCommandAndSubCommands(cmdToDts,cmd,mcmdsArray[i].split("\\:")[1]);
            addCommandAndSubCommands(cmdToDts,cmd,mcmdsArray[i].split(REGX_split_on_colons_but_not_in_single_quotes)[1]);
        }
        return cmdToDts;
    }

    /**
     * Take a command (e.g. F, L, B, RAMP, or OOGABOOGA) and its preParsedDirectionTime
     * subcommands (e.g. +1, -1|-1, -1, 'R:512', or 'MD:0'|'MT:-512') and create a hashmap
     * of the command to an array of the subcommands
     * @param hm hashmap to update
     * @param cmd e.g. e.g. F, L, B, RAMP, or OOGABOOGA
     * @param subCommandsString e.g. +1, -1|-1, -1, 'R:512', or 'MD:0'|'MT:-512'
     * Adds the command (as a key) and its associated sub-commands (as an array value) to hashmap
     */
    public static void addCommandAndSubCommands(HashMap hm, String cmd, String subCommandsString)
    {
        // DTs refers to direction (+/-) and time (1, 2, etc) which are the common commands,
        // although there are single-quoted commands ('R:512') which are not associated with
        // time or direction and which are saved as-is (including the single quotes)
        //
        List<String> subCommandDTs = new ArrayList<String>();
        String vals[] = subCommandsString.split("\\|");
        // val[] is something like +1 or -1 +1 'MD:1047'
        for( int j=0;j<vals.length;j++)
        {
             if(! vals[j].startsWith("'"))
             {
                String d = vals[j].substring(0, 1);
                String t = vals[j].substring(1);
                // dTs is something like +,1 or -,0.5
                subCommandDTs.add(d + "," + t);
            }
            else
            {
                 subCommandDTs.add(vals[j]);
            }
        }
        //HashMap cmdToDts = new HashMap();
        hm.put(cmd, subCommandDTs);
    }
	public Object get(String key)
	{
        String test = (String)rawProps.get(key);
        if( test == null )
        {
            test = (String)rawProps.get(mover_config+"_"+key);
            if( test == null)
            {
                logger.warn("Cannot find property [" + key + "] or [" + mover_config + "_" + key + "]");
                return null;
            }
            else
            {
                key = mover_config +"_"+key;
            }
        }
		return rawProps.getProperty(key);
	}
	public void set(String key, String val) {
        // major hack to account for motor alias string
        String test = (String)rawProps.get(key);
        if( test == null )
        {
            test = (String)rawProps.get(mover_config+"_"+key);
            if( test == null)
            {
                logger.warn("Cannot find property [" + key + "] or [" + mover_config + "_" + key + "]");
            }
            else
            {
                key = mover_config +"_"+key;
            }
        }
		rawProps.setProperty(key, val);
        try {
            setMotorVariables(optionalPropsPath, false);
        }
        catch(IOException e)
        {
            logger.error("******* COULD NOT SET " + key + "/" + val,e);
        }
	}
}
