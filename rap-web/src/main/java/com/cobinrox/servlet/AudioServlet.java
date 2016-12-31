package com.cobinrox.servlet;

import com.cobinrox.common.Utils;
/*
import com.issinc.hackathon.Color2AudioFileMatcher;
import com.issinc.hackathon.ImageAnalyzer;
import com.issinc.hackathon.VideoAudioController;
*/
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.*;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * Created by name on 5/22/2015.
 *
 * Use
 * mode 180,60
 * on intellij console
 *
 *
 * Using maven:
 * Change to the rap-web directory
 * run:  mvn jetty:run
 * On browser for raw servlet execution go to:
 * http://localhost:8080/servlets/AudioServlet
 *
 * On browser for index.html execution go to:
 * http://localhost:8080
 *
 *
 */
//@javax.servlet.annotation.WebServlet(name = "AudioServlet")
public class AudioServlet extends javax.servlet.http.HttpServlet {
    //VideoAudioController vac;
    static final Logger logger = Logger.getLogger(AudioServlet.class);
    static boolean stupid;
    ServletConfig servletConfig;

    /**
     * Called by container the first time when the servlet is invoked
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
        System.err.println("INIT INIT INIT");
        servletConfig = config;
        String dir = System.getProperty("user.dir");
        System.err.println("Current dir = " + dir);
        Enumeration e = Logger.getRootLogger().getAllAppenders();
        while ( e.hasMoreElements() ){
            Appender app = (Appender)e.nextElement();
            if ( app instanceof FileAppender){
                System.err.println("LogFile: " + ((FileAppender)app).getFile());
            }
        }
        String cp = System.getProperty("java.class.path");
        String[] cps = cp.split(";");
        for(String c:cps)
        {
            System.out.println(c);
        }
    }
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws ServletException, IOException {
        if(! stupid )
        {
            String dir = System.getProperty("user.dir");
            System.out.println("current dir = " + dir);
            stupid = true;
            Enumeration e = Logger.getRootLogger().getAllAppenders();
            while ( e.hasMoreElements() ){
                Appender app = (Appender)e.nextElement();
                if ( app instanceof FileAppender){
                    System.out.println("LogFile: " + ((FileAppender)app).getFile());
                }
            }
        }
        System.out.println("POST " + request.getParameter("leCmd"));
        logger.info("POST HOO HAH " + request.getParameter("leCmd"));
        //////////if( vac == null ) vac = new VideoAudioController();
        String res = "";
        try
        {
            ////////////vac.playAudioFile(request.getParameter("leCmd"));
            res = "Command complete.";
        }
        catch(Throwable t)
        {
            logger.error("Sending audio file command",t);
            res = "ERROR SENDING AUDIO FILE COMMAND\n" + getStackTraceAsString(t);
        }

        /*
        response.setContentType("application/json");
// Get the printwriter object from response to write the required json object to the output stream
PrintWriter out = response.getWriter();
// Assuming your json object is **jsonObject**, perform the following, it will return your json object
out.print(jsonObject);out.write(your_string.getBytes("UTF-8"));
out.flush();
         */
        request.getRequestDispatcher(request.getParameter("viewid")).forward(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws ServletException, IOException {
        String leCmd = request.getParameter("leCmd"); // get param from caller
        logger.info("In doGet, with request [" + leCmd + "]");
        response.setContentType("application/json");
        String responseStr = "";
        if( leCmd != null )
        {
            ///////////////if( vac == null ) vac = new VideoAudioController();
            if(leCmd.equals("imagerecon"))
            {
                responseStr = getAudioFileNamePerImage();
                if(isAudioFile(responseStr))
                {
                    responseStr = playAudioFile(responseStr, request.getSession().getServletContext());
                }
            }
            else if( isAudioFile(leCmd))
            {
                responseStr = playAudioFile(leCmd, request.getSession().getServletContext());
            }
            else
            {
                responseStr = "Command [" + leCmd + "] not yet implemented";
            }
        }
        else
        {
            responseStr = "No command provided";
        }
        responseStr = Utils.stringToJsonString("playAudioCmdResult",responseStr);//"{\"response\":\""+ responseStr + "\"}";
        logger.info("Returning JSON string [" + responseStr + "]");
        response.setContentType("application/json");
        response.getWriter().write(responseStr);

       /* String json = new Gson().toJson(personData);
        response.setContentType("application/json");
        response.getWriter().write(json);*/
    }
    private String getAudioFileNamePerImage()
    {
        String responseStr = null;
        try
        {
            String colorName=null;
            try
            {
                ///////////////colorName = ImageAnalyzer.getColorName("target/classes/img.jpg");
            }
            catch(Throwable t)
            {
                logger.info("Image file not at target/classes/img.jpg");
                try
                {
                    ///////////////colorName = ImageAnalyzer.getColorName("img.jpg");
                }
                catch(Throwable t2)
                {
                    logger.info("Image file not found at img.jpg");
                }
            }
            ////////////////responseStr = Color2AudioFileMatcher.getAudioFileName(colorName);

            logger.info(colorName + " = " + responseStr);
        }
        catch(Throwable t)
        {
            logger.error("Matching image to filename",t);
            responseStr = "ERROR MATCHING IMAGE TO SOUNDFILE\n" + getStackTraceAsString(t);
        }
        return responseStr;
    }
    private boolean isAudioFile(String possibleFileName)
    {
        String lowerCaseName = possibleFileName.toLowerCase();
        if(lowerCaseName.endsWith(".wav") || lowerCaseName.endsWith(".mp3")) return true;
        return false;
    }
    private String getStackTraceAsString(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }    private static String web_inf_path;    private static final String WEB_INF_DIR_NAME="WEB-INF";

    public  String getWebInfPath() throws UnsupportedEncodingException {
        if (web_inf_path == null) {
            web_inf_path = URLDecoder.decode(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF8");
            web_inf_path=web_inf_path.substring(0,web_inf_path.lastIndexOf(WEB_INF_DIR_NAME)+WEB_INF_DIR_NAME.length());
        }
        return web_inf_path;
    }
    private String playAudioFile(String fileName, ServletContext sc)
    {
        String responseStr = null;
        try
        {
            String junk = getWebInfPath() + "/classes/" + fileName;
            File junkFile = new File(junk);
            logger.info("Audio file [" + junkFile.getAbsolutePath() + "] exists: " + junkFile.exists());
            /////////////vac.playAudioFile(fileName);
            responseStr = "Command complete.";
        }
        catch(Throwable t)
        {
            logger.error("Sending audio file command",t);
            responseStr = "ERROR SENDING AUDIO FILE COMMAND\n" + getStackTraceAsString(t);
        }
        return responseStr;
    }
}
