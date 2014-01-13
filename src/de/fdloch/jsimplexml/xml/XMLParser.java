/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jsimplexml.xml;

import java.io.IOException;
import java.net.URL;
import de.fdloch.jsimplexml.inet.InetUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Florian
 */
public class XMLParser {

    private String xml;
    private XMLNode rootNode;

    public XMLParser() {
        this("");
    }
    
    public XMLParser(File file) throws FileNotFoundException, IOException {
        this("");
        
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        
        FileReader fR = new FileReader(file);
        BufferedReader bR = new BufferedReader(fR);
        
        StringBuilder sB = new StringBuilder();
        while (true) {
            String line = bR.readLine();
            if (line != null) {
                sB.append(line);
            }
            else {
                break;
            }
        }
        
        bR.close();
        fR.close();
        
        this.setXML(sB.toString());
    }    
    
    public XMLParser(URL url) throws IOException {
        this(InetUtils.getXMLFromURL(url));
    }
    
    public XMLParser(String xml) {
        this.xml = xml;
    }
    
    public void setXML(String xml) {
        this.xml = xml;
    }
    
    public String getXML() {
        return this.xml;
    }    
    
    public XMLNode parseXML() {
        //Check if the document is a compliant xml (check if there is a prolog)
        if (this.xml.startsWith("<?xml")) {
        
            //Start the recursive function
            this.parseBlock_recursive(null,0);
        
            return this.rootNode;
        }
        else {
            return null;
        }
    }
    
    private int parseBlock_recursive(XMLNode parentNode, int pos) {
        XMLSegment nextSgmt, thisSgmt;
        boolean subElementFound = false; //If an element has no subelements it propably got a value
        
        //Get the opening Tag of that block
        thisSgmt = findNextSegment(pos); 
        
        //If the tag is a comment or the prolog-tag -> ignore it! (Go to the next next segment) 
        while (thisSgmt.isCommentTag() || thisSgmt.wholeTag.startsWith("<?xml")) {
            pos = thisSgmt.endPos + 1;
            thisSgmt = findNextSegment(pos);
        }
        
        XMLNode node = new XMLNode(thisSgmt.type);
        node.setParameterMap(thisSgmt.getParameter());
        pos = thisSgmt.endPos + 1;
        
        //If there is no parent node, this node it the root node and therefore is linked to the field "rootNode"
        if (parentNode == null) {
            this.rootNode = node;
        }
        else {
            parentNode.addChildNode(node); //If there is a parent node, this node should be added as its child
        }
        
        while (true) {
            nextSgmt = findNextSegment(pos);
            
            //If the next Segment (findNextSegment() can only find an opening or a closing tag resp. a standalone tag) is an opening
            //tag, then the recursive call is made. It is noted down that a subelement has been found in this block (First else-if block).
            //In the first if-block (following hereafter) it is checked, if the tag is a standalone tag (like e.g. <hr/>). If so, this tag needs no recursive call
            //because it can not have subelements
            if (nextSgmt.isOpeningTag() && nextSgmt.isStandaloneTag()) {
                XMLNode newNode = new XMLNode(nextSgmt.type);
                newNode.setParameterMap(nextSgmt.getParameter());
                
                node.addChildNode(newNode);
                
                subElementFound = true;
                
                pos = nextSgmt.endPos + 1;
            } 
            else if (nextSgmt.isOpeningTag()) {               
                pos = parseBlock_recursive(node, pos);
                subElementFound = true;
            }
            //If the next tag is a closing tag it has to be the closing tag of the current block (at least in a valid XML-document!)
            else if (!nextSgmt.isOpeningTag()) { //Closing-Itself-Tag resp. closing the current block
                //If there weren't any subelements, there might be some content resp. value
                if (!subElementFound) {
                    String value = this.xml.substring(thisSgmt.endPos + 1, nextSgmt.startPos);
                    value = cleanValue(value);
                    node.setValue(value);
                }
                
                return nextSgmt.endPos + 1;
            }
        }
    }
    
    private String cleanValue(String str) {
        str = str.trim();
        
        if (str.startsWith("\n")) { //For UNIX (and Mac OS X)
            str = str.substring(1);
        }
        else if (str.startsWith("\r\n")) { //For Windows
            str = str.substring(2);
        }    
        else if (str.startsWith("\r")) { //For old Mac OS
            str = str.substring(1);
        }
        
        return str;
    }
    
    private XMLSegment findNextSegment(int offset) {
        int start = -1, end = -1;
        String type, wholeTag;
        boolean insideString = false;
        boolean insideComment = false;
        
        //start = this.xml.indexOf('<', offset);
        for (int i = offset; i < this.xml.length() && start == -1; i++) {
            char c = this.xml.charAt(i);
            if (c == '"') {
                insideString = !insideString;
            }
            //Check if we just found a comment block (this is needed because we need to be careful when searching the end tag. (We explicitly search for the 
            //-->, because other tags could be placed in a comment block)
            else if (i + 3 < this.xml.length() && c == '<' && this.xml.charAt(i+1) == '!' && this.xml.charAt(i+2) == '-' && this.xml.charAt(i+3) == '-') {
                insideComment = true;
                start = i;
            }
            else if (c == '<' && !insideString) {
                start = i;
            }            
        }
        
        if (start < 0) {
            return null;
        }
        
        //The end-detection is quite advanced with the following code. If the '>'
        for (int i = start + 1; i < this.xml.length() && end == -1; i++) {
            char c = this.xml.charAt(i);
            if (c == '"') {
                insideString = !insideString;
            }
            //If we are inside a comment block we check for the end of the comment block - not for the normale ">", because this could be used
            //inside the comment. Also the comment-end-symbol (-->) will be ignored when encapsulated ("-->")
            else if (!insideString && insideComment && i + 2 < this.xml.length() && c == '-' && this.xml.charAt(i+1) == '-' && this.xml.charAt(i+2) == '>') {
                end = i + 2;
            }
            else if (c == '>' && !insideString && !insideComment) {
                end = i;
            }
        }
//        end = this.xml.indexOf('>', start + 1);
        
        if (end < 0) {
            return null;
        }
        
        int startOfType = (this.xml.charAt(start + 1) == '/') ? start + 2 : start + 1;
        int endOfType = end; //Gets overwritten if a blank space is found in the tag by the following loop (a blank separates the type from the parameters)
        for (int i = startOfType; i < end; i++) {
            if (this.xml.charAt(i) == ' ') {
                endOfType = i;
                break;
            }
        }
        
        type = this.xml.substring(startOfType, endOfType);
        wholeTag = this.xml.substring(start, end + 1);
        
        return new XMLSegment(type, wholeTag, start, end);
    }
}
