/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jsimplexml.xml;

import java.util.HashMap;

/**
 *
 * @author Florian
 */
public class XMLSegment {
    
    public String type;
    public String wholeTag;
    public int startPos;
    public int endPos;
    private HashMap<String, String> parameter;

    public XMLSegment(String type, String wholeTag, int startPos, int endPos) {
        this.type = type;
        this.wholeTag = wholeTag;
        this.startPos = startPos;
        this.endPos = endPos;
        this.parameter = new HashMap<String, String>();
    }
    
    private boolean isTag() {
        if (this.wholeTag.startsWith("<") && this.wholeTag.endsWith(">")) {
            return true;
        }
        return false;
    }
    
    public boolean isOpeningTag() {
        if (this.isTag() && !this.wholeTag.startsWith("</")) {
            return true;
        }
        return false;
    }
    
    public boolean isStandaloneTag() {
        if (this.isTag() && this.wholeTag.endsWith("/>")) {
            return true;
        }
        return false;
    }
    
    public boolean isCommentTag() {
        if (this.wholeTag.startsWith("<!--")) {
            return true;
        }
        return false;
    } 
    
    public HashMap<String, String> getParameter() {
        //Only opening tags have parameter
        if (this.isOpeningTag()) {
            //Parse the tag if this is the first request for the parameter. If it is not the first, then the parsing
            //has already been done and the parameter are stored in the accordingly-name field.
            if (this.parameter.isEmpty()) {
                boolean inKey = false;
                String key = "";
                boolean inValue = false;
                String value = "";
                
                for (int i = this.type.length() + 1; i < this.wholeTag.length() - 1; i++) { //The whole tag includes < resp. </ and > (-1 because ">" needs not to be checked)
                    char c = this.wholeTag.charAt(i);
                    
                    if (c == ' ' && !inValue) { //Blanks are allowed in the value string
                        inKey = true;
                    }
                    else if (c == '=') {
                        inKey = false;
                    }
                    else if (c == '"' && !inKey) { //!inKey isn't needed - just to prevent, that a value is found before the end of the key is reached
                        inValue = !inValue;
                        
                        //When the key-value-pair has been found this parameter is finished
                        //All vars have default values after this step
                        if (!inValue) {
                            this.parameter.put(key, value);
                            key = "";
                            value = "";
                        }
                    }
                    else {
                        //If the generator forgot the blank after the previous parameter, than we will still interpret this character as the first one of a new key
                        if (!inKey && !inValue) {
                            inKey = true;
                        }
                        
                        if (inKey) {
                            key = key + c;
                        }
                        else if (inValue) {
                            value = value + c;
                        }
                    }
                }
            }

            return this.parameter;
        }
        else {
            return null;
        }
    }
}
