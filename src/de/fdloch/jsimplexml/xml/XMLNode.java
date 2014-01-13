/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jsimplexml.xml;

import de.fdloch.jsimplexml.util.KeyValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Florian
 */
public class XMLNode implements Iterable<XMLNode> {
    
    private String type;
    private String value;
    private ArrayList<XMLNode> childs;
    private HashMap<String, String> parameter;

    public XMLNode(String type, String value) {
        this.type = type;
        this.value = value;
        this.childs = new ArrayList<XMLNode>();
        this.parameter = new HashMap<String, String>();
    }

    public XMLNode(String type) {
        this(type, "");
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void addChildNode(XMLNode childNode) {
        this.childs.add(childNode);
    }
    
    public boolean addParameter(KeyValue<String, String> kV) {
        boolean containsKeyAlready = this.parameter.containsKey(kV.getKey());
        
        this.parameter.put(kV.getKey(), kV.getValue());
        
        return containsKeyAlready;
    }
    
    public void setParameterMap(HashMap<String, String> parameter) {
        this.parameter = parameter;
    }
    
    public String getParameter(String key) {
        if (this.parameter.containsKey(key)) {
            return this.parameter.get(key);
        }
        else {
            return null;
        }
    }
    
    public HashMap<String, String> getParameterMap() {
        return this.parameter;
    }    
    
    public String getType() {
        return this.type;
    }
    
    public int getChildsNum() {
        return this.childs.size();
    }
    
    public XMLNode getChildNode(int index) {
        if (index < this.childs.size()) {
            return this.childs.get(index);
        }
        
        return null;
    }
    
    public XMLNode[] getChildNodesByType(String type) {
        return this.getChildNodesByType(type, 0);
    } 
    
    public XMLNode[] getChildNodesByType(String type, int offset) {
        ArrayList<XMLNode> list = new ArrayList<XMLNode>();
        
        for (int i = offset; i < this.childs.size(); i++) {
            if (this.childs.get(i).getType().equals(type)) {
                list.add(this.childs.get(i));
            }
        }
        
        XMLNode[] ar = new XMLNode[list.size()];
        
        for (int i = 0; i < ar.length; i++) {
            ar[i] = list.get(i);
        }
        
        return ar;
    }    
    
    public int countChildNodesForType(String type) {
        int a = 0;
        
        for (int i = 0; i < this.childs.size(); i++) {
            if (this.childs.get(i).getType().equals(type)) {
                a++;
            }
        }
        
        return a;
    }

    public boolean removeChild(int index) {
        if (this.childs.size() > index) {
            this.childs.remove(index);
            return true;
        }
        
        return false;
    }
    
    public boolean removeChild(XMLNode child) {
        return this.childs.remove(child);
    }
    
    public void removeAllChildren() {
        for (int i = 0; i < this.childs.size(); i++) {
            this.childs.remove(0);
        }
    }
    
    @Override
    public Iterator<XMLNode> iterator() {
        return new Iterator<XMLNode>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                if (this.index < childs.size()) {
                    return true;
                }
                return false;
            }

            @Override
            public void remove() {
                //
            }

            @Override
            public XMLNode next() {
                this.index++;
                return childs.get(this.index - 1);
            }
        };
    }
    
}
