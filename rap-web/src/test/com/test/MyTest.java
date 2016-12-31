package com.test;

import com.cobinrox.common.Utils;
import com.cobinrox.io.impl.usb.DumpDevices;
import junit.framework.TestCase;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.usb4java.LibUsbException;

import java.io.InputStream;

public class MyTest extends TestCase
{

    private static final String RESOURCES_URL = "/rs";
    private static final String CONTEXT = "/app_context";
    private static final String DS_CONFIG = "/jetty-ds-test.xml";
    private String baseResourceUrl;

    public void testThisEh() throws Exception
    {
        String sys = Utils.getSystemInfo();
        System.out.println(sys);
        Server server = new Server(0);   // see notice 1
        server.setHandler(new WebAppContext("src/main/webapp", CONTEXT)); // see notice 2

        /* see notice 3
        InputStream jettyConfFile = InboxTest.class.getResourceAsStream(DS_CONFIG);
        if (jettyConfFile == null) {
            throw new FileNotFoundException(DS_CONFIG);
        }
        XmlConfiguration config = new XmlConfiguration(jettyConfFile);
        config.configure(server);

        server.start();

        // see notice 1
        int actualPort = server.getConnectors()[0].getLocalPort();
        baseResourceUrl = "http://localhost:" + actualPort + CONTEXT + RESOURCES_URL;
        */

        try {
            DumpDevices.main(null);
        }
        catch( LibUsbException l)
        {
            if(l.getMessage().contains("USB error 5"))
            {
                System.out.println("**** Warning");
                l.printStackTrace();
                System.out.println("**** Warning");
            }
        }
    }
}
