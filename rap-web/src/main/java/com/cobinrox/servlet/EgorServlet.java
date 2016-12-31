package com.cobinrox.servlet;

import com.cobinrox.common.Utils;
import com.cobinrox.io.DoMotorCmd;
import com.cobinrox.io.impl.gpio.EzWiringPiGpioPulse;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
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
 * run:  maven jetty:run
 * On browser for raw servlet execution go to:
 * http://localhost:8080/servlets/EgorServlet
 *
 * On browser for index.html execution go to:
 * http://localhost:8080
 *
 *
 */
//@javax.servlet.annotation.WebServlet(name = "EgorServlet")
public class EgorServlet extends javax.servlet.http.HttpServlet {
    DoMotorCmd dmc;
    static final Logger logger = Logger.getLogger(EgorServlet.class);
    static boolean stupid;
    ServletConfig servletConfig;

    /**
     * Called by container the first time when the servlet is invoked
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
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
    }
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
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
        if( dmc == null ) dmc = new DoMotorCmd();
        dmc.doThis(request.getParameter("leCmd"));
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
    EzWiringPiGpioPulse ep;
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String leCmd = request.getParameter("leCmd"); // get param from caller
        logger.info("In doGet, with request [" + leCmd + "]");
        response.setContentType("application/json");
        String responseStr = "";
        if( leCmd != null )
        {
            if( dmc == null ) dmc = new DoMotorCmd();
            responseStr = dmc.doThis(request.getParameter("leCmd"));
        }
        else
        {
            responseStr = "No command provided";
        }
        responseStr = Utils.stringToJsonString("doCmdResult",responseStr);//"{\"response\":\""+ responseStr + "\"}";
        logger.info("Returning JSON string [" + responseStr + "]");
        response.setContentType("application/json");
        response.getWriter().write(responseStr);


       /* String json = new Gson().toJson(personData);
        response.setContentType("application/json");
        response.getWriter().write(json);*/
    }
}
