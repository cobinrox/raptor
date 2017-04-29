package com.cobinrox.common; 
import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Utils {
/* test */
	static final Logger logger = Logger.getLogger(Utils.class);
	static JSONParser jasonParser = new JSONParser();

	public static String getSystemInfo()
	{
		StringBuffer sb = new StringBuffer("System Properties\n");
		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)p.get(key);
			sb.append(key + " : [" + value + "]\n");
		}
		return sb.toString();
	}
	public static String stringToJsonString(String key, String value)
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(key, value);
		return jsonObj.toString();
	}
	public static List<String> getStringsFromJsonArrayWithKey(String inputJsonStr, String key) throws Throwable
	{
		try {
			JSONObject jObj = (JSONObject) jasonParser.parse(inputJsonStr);
			JSONArray jsonArray = (JSONArray)jObj.get(key);
			List<String> list = new ArrayList<String>();
			for (int i=0; i<jsonArray.size(); i++) {
				list.add( (String)jsonArray.get(i) );
			}
			return list;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			String err = "Parsing JSON string [" + inputJsonStr + "] for key [" + key + "] " ;
			logger.error(err,t);
			throw new IOException(err + Utils.getStackTraceAsString(t));
		}
	}
	public static HashMap getHashMapFromJsonStr(String inputJsonStr) throws Throwable
	{
		HashMap hm = new HashMap();
		try {
			JSONObject jObj = (JSONObject) jasonParser.parse(inputJsonStr);
			Set keySet = jObj.keySet();
			for(Object key: keySet)
			{
				JSONArray jsonArray = (JSONArray)jObj.get(key);
				List<String>list = new ArrayList<String>();
				for (int i=0; i<jsonArray.size(); i++) {
					list.add( (String)jsonArray.get(i) );
				}
				hm.put(key,list);
			}

			return hm;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			String err = "Parsing JSON string [" + inputJsonStr + "]" ;
			logger.error(err,t);
			throw new IOException(err + Utils.getStackTraceAsString(t));
		}
	}
	/**
	 * Return properties, given prefix of a properties file residing in
	 * the current users home dir or classpath.
	 * @param caller
	 * @param prefixFileName
	 * @return
	 */
	public static Properties readProps(String optionalPropsFilePath, Object caller, String prefixFileName)
	{
		Properties p = null;
		if( optionalPropsFilePath != null )
		{
			prefixFileName = optionalPropsFilePath.substring(0,optionalPropsFilePath.indexOf(".properties"));
			File test = new File(optionalPropsFilePath);
			if(!test.exists())
			{
				System.err.println("NOTE your props file [" + optionalPropsFilePath + "] (" + test.getAbsolutePath() + ") does not appear to exist");
			}
			p = new Properties();
			Reader r = null;
			try
			{
				r = new FileReader(test);
				p.load(r);
				return p;
			}
			catch(Throwable t)
			{
				logger.error("Reading props file [" + optionalPropsFilePath + "]",t);
				return null;
			}
			finally
			{
				if( r != null )try{r.close();}catch(Throwable t){}
			}
		}
		p = getPreviousPropsFromUserDirIfAny(prefixFileName);
		if( p != null ) return p;
		p = getPreviousPropsFromWorkingDirIfAny(prefixFileName);
		if( p !=null ) return p;
		p = new Properties();
		String fname = prefixFileName + ".properties";
		InputStream is = null;
		try
		{
			System.out.println("Working Dir:" + System.getProperty("user.dir"));
			String[] cps = System.getProperty("java.class.path").split(";");
			if( cps.length < 10 )
				cps = System.getProperty("java.class.path").split(":");
			System.out.println("C L A S S   P A T H");
			for(String cp:cps)
			{
				System.out.println(cp);
			}
			System.out.println("======================================================");
			logger.debug("classpah: [" + System.getProperty("java.class.path") + "]");
			logger.debug("fname: " + fname);
			logger.debug("caller: " + caller);;
			logger.debug("caller.getClass(): " + caller.getClass());
			is = caller.getClass().getClassLoader().getResourceAsStream(fname);//new FileInputStream(fname);
			
			logger.debug("caller.getClass().getClassLoader().getResourceAsStream(fname):" + is);

			if( is != null)
			{
				URL possiblefile = null;
				try
				{
					possiblefile = caller.getClass().getClassLoader().getResource(fname);
				} catch(Throwable t){} // ok, may be stream within a jar file
				logger.info("Reading from resource [" + fname + "]" +
								(possiblefile==null?"":" ["+possiblefile.getPath()+"]"));
				p.load(is);
			}
			else
			{
				throw new IOException("Cannot read input stream for " + fname);
			}
			return p;
		}
		catch(Throwable t)
		{
			logger.error("Reading/accessing properties file [" + fname + "]",t);
			logger.error("Classpath is [" + Utils.classpath() + "]");
			return p;
		}
		finally
		{
			if( is != null )
			{
				try{is.close();}catch(Throwable t){}
			}
		}
	}
    public static String determinePropsFile(String optionalPropsFilePath, Object caller, String prefixFileName)
    {
        Properties p = null;
        if( optionalPropsFilePath != null )
        {
            File testFile = new File(optionalPropsFilePath);
            if(!testFile.exists())
            {
                logger.warn("NOTE your props file [" + optionalPropsFilePath + "] does not appear to exist");
            }
            p = new Properties();
            Reader r = null;
            try
            {
                r = new FileReader(testFile);
                p.load(r);
                return testFile.getAbsolutePath();
            }
            catch(Throwable t)
            {
                logger.error("Reading props from file [" + optionalPropsFilePath + "]",t);
                return null;
            }
            finally
            {
                if( r != null )try{r.close();}catch(Throwable t){}
            }
        }
        p = getPreviousPropsFromUserDirIfAny(prefixFileName);
        if( p != null ) return new File(System.getProperty("user.home") + File.separator + prefixFileName+".properties").getAbsolutePath();
        p = getPreviousPropsFromWorkingDirIfAny(prefixFileName);
        if( p !=null ) return new File(System.getProperty("user.dir") + File.separator + prefixFileName+".properties").getAbsolutePath();
        p = new Properties();
        String fname = prefixFileName + ".properties";
        InputStream is = null;
        try
        {
            System.out.println("Working Dir:" + System.getProperty("user.dir"));
            String[] cps = System.getProperty("java.class.path").split(";");
            if( cps.length < 10 )
                cps = System.getProperty("java.class.path").split(":");
            System.out.println("C L A S S   P A T H");
            for(String cp:cps)
            {
                System.out.println(cp);
            }
            System.out.println("======================================================");
            logger.debug("classpah: [" + System.getProperty("java.class.path") + "]");
            logger.debug("fname: " + fname);
            logger.debug("caller: " + caller);;
            logger.debug("caller.getClass(): " + caller.getClass());
            is = caller.getClass().getClassLoader().getResourceAsStream(fname);//new FileInputStream(fname);

            logger.debug("caller.getClass().getClassLoader().getResourceAsStream(fname):" + is);

            if( is != null)
            {
                URL possiblefile = null;
                try
                {
                    possiblefile = caller.getClass().getClassLoader().getResource(fname);
                } catch(Throwable t){} // ok, may be stream within a jar file
                logger.info("Reading from resource [" + fname + "]" +
                        (possiblefile==null?"":" ["+possiblefile.getPath()+"]"));
                p.load(is);
                return possiblefile.toString();
            }
            else
            {
                throw new IOException("Cannot read input stream for " + fname);
            }
        }
        catch(Throwable t)
        {
            logger.error("Reading/accessing properties file [" + fname + "]",t);
            logger.error("Classpath is [" + Utils.classpath() + "]");
            return null;
        }
        finally
        {
            if( is != null )
            {
                try{is.close();}catch(Throwable t){}
            }
        }
    }
	public static String delOldPropsFileFromUserDir(String prefix)
	{
		String userHomeDirName = System.getProperty("user.home");
		logger.info("user home dir: [" + userHomeDirName + "]");
		File propsFile = new File(userHomeDirName,prefix+".properties");
		boolean deleted = propsFile.delete();
		return "Deleted [" + propsFile.getAbsolutePath() + "]: " + deleted;
	}
	public static String savePropsToUserDir(Properties p, String prefix)
	{
		String userHomeDirName = System.getProperty("user.home");
		logger.info("user home dir: [" + userHomeDirName + "]");
		File propsFile = new File(userHomeDirName,prefix+".properties");
		propsFile.delete();
		FileOutputStream fos = null;
		String ret = null;
		try
		{
			fos = new FileOutputStream(propsFile);
			SortedProperties tmp = new SortedProperties();
			tmp.putAll(p);
			tmp.store(fos, "");


			//p.store(fos,"");
			ret = propsFile.getAbsolutePath();
		}
		catch(Throwable t)
		{
			ret = "Unable to save props file [" + propsFile.getAbsolutePath() + "] " + t.getMessage();
			logger.info(ret,t);
		}
		finally
		{
			if( fos != null )
			{
				try{fos.close();}catch(Throwable t){}
			}
		}
		return ret;

	}
	public static Properties getPreviousPropsFromUserDirIfAny(String prefix)
	{
		String userHomeDirName = System.getProperty("user.home");
		logger.info("user home dir: [" + userHomeDirName + "]");
		logger.info("Checking home dir for existing properties file...");
		File oldPropsFile = new File(userHomeDirName,prefix+".properties");
		if( !oldPropsFile.exists())
		{
			logger.info("No previous props file found in user home dir");
			return null;
		}
		logger.info("Reading props from previous props file [" + oldPropsFile.getAbsolutePath() + "]");
		//ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			FileInputStream fin = new FileInputStream(oldPropsFile);
			//ois = new ObjectInputStream(fin);
			Properties p = new Properties();
			p.load(fin);
			return p;
		}
		catch(Throwable t)
		{
			logger.info("Could not read previous props file [" + oldPropsFile.getAbsolutePath() + "]",t);
			return null;
		}
		finally
		{
			if(fis!=null)
			{
				try{fis.close();}catch(Throwable t){}
			}
		}
	}
	public static String classpath()
	{
		return System.getProperty("java.class.path");
	}
	public static String getStackTraceAsString(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	public static Properties getPreviousPropsFromWorkingDirIfAny(String prefix)
	{
		String userWorkDirName = System.getProperty("user.dir");
		logger.info("user working dir: [" + userWorkDirName + "]");
		logger.info("Checking working dir for existing properties file...");
		File oldPropsFile = new File(userWorkDirName,prefix+".properties");
		if( !oldPropsFile.exists())
		{
			logger.info("No previous props file found in user work dir");
			return null;
		}
		logger.info("Reading props from previous props file [" + oldPropsFile.getAbsolutePath() + "]");
		//ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			FileInputStream fin = new FileInputStream(oldPropsFile);
			//ois = new ObjectInputStream(fin);
			Properties p = new Properties();
			p.load(fin);
			return p;
		}
		catch(Throwable t)
		{
			logger.info("Could not read previous props file [" + oldPropsFile.getAbsolutePath() + "]",t);
			return null;
		}
		finally
		{
			if(fis!=null)
			{
				try{fis.close();}catch(Throwable t){}
			}
		}
	}
}
class SortedProperties extends Properties
{
	@Override
	public synchronized Enumeration<Object> keys() {
		return Collections.enumeration(new TreeSet<Object>(super.keySet()));
	}
}
