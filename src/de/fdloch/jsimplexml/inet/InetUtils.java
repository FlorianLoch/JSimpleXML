/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jsimplexml.inet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 *
 * @author Florian
 */
public class InetUtils {
    
    public static String getXMLFromURL(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setUseCaches(false);
        conn.connect();
        InputStream iS = conn.getInputStream();
        
        BufferedReader bR = new BufferedReader(new InputStreamReader(iS, Charset.forName("UTF-8"))); //Character encoding is needed for execution in Tomcat (and only there!?!)
        
        StringBuilder xml = new StringBuilder("");
        String line;
        
        while ((line = bR.readLine()) != null) {
            xml.append(line);
        }
        
        return xml.toString();
    }
    
}
