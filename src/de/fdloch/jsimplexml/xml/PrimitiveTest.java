/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jsimplexml.xml;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Florian
 */
public class PrimitiveTest {
    public static void main(String[] args) throws IOException {
        XMLNode root = new XMLNode("html");
        XMLNode child = new XMLNode("body");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("onload", "init();");
        params.put("onclick", "hallo();");
        child.setParameterMap(params);
        root.addChildNode(child);
        XMLNode urenkel = new XMLNode("p", "Das ist mein Text!");
        child.addChildNode(urenkel);
        
        XMLUtils.printTree(root, "\t");
        System.out.println("------------");
        XMLUtils.writeAsXMLToFile(root, null, "\t");
    }
}
