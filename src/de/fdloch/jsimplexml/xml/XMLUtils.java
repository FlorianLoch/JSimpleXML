/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jsimplexml.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Florian
 */
public class XMLUtils {
    
    public static void printTree(XMLNode node, String lineIndent) {
        printNode(node, "", lineIndent);
    }
    
    private static void printNode(XMLNode node, String lineIndentTotal, String lineIndent) {
        String out;
        
        out = lineIndentTotal + node.getType();
        
        HashMap<String, String> param = node.getParameterMap();
        if (!param.isEmpty()) {
            out += " (";
        }
        Iterator<String> it = param.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            out += key + "=\"" + param.get(key) + "\"";
            if (it.hasNext()) {
                out += " ";
            }
        }
        if (!param.isEmpty()) {
            out += ")";
        }        
        
        if (!node.getValue().isEmpty()) {
            out += ": " + node.getValue();
        }

        System.out.println(out);
        
        for (XMLNode child : node) {
            printNode(child, lineIndentTotal + lineIndent, lineIndent);
        }
    }
    
    public static void writeAsXMLToFile(XMLNode root, File f, String lineIndent) throws IOException {
        FileWriter fW = new FileWriter(f);
        
        StringBuilder out = new StringBuilder();
        out.append("<?xml>");
        printNodeAsXML(root, out, "", lineIndent);
        
        fW.write(out.toString());
        fW.flush();
        fW.close();
        
        System.out.println(out.toString());
    }    
    
    private static void printNodeAsXML(XMLNode node, StringBuilder out, String lineIndentTotal, String lineIndent) {
        out.append("\n");
        out.append(lineIndentTotal);
        out.append("<");
        out.append(node.getType());
        
        HashMap<String, String> param = node.getParameterMap();
        Iterator<String> it = param.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            key = " " + key + "=\"" + param.get(key) + "\"";
            out.append(key);
        }
        
        out.append(">");
        
        if (!node.getValue().isEmpty()) {
            out.append("\n");
            out.append(lineIndentTotal);
            out.append(lineIndent);
            out.append(node.getValue());
        }
        
        for (XMLNode child : node) {
            printNodeAsXML(child, out, lineIndentTotal + lineIndent, lineIndent);
        }
        
        out.append("\n");
        out.append(lineIndentTotal);
        out.append("</");
        out.append(node.getType());
        out.append(">");
    }    
    
}
