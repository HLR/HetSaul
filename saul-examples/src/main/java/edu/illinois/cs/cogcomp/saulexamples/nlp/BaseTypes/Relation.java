/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import java.util.*;

/**
 * Created by Taher on 2016-12-25.
 */
public class Relation {
    private String id;
    private NlpBaseElement parent;
    private Map<String, String> properties = new HashMap<>();
    private Map<Integer, String> argumentIds = new HashMap<>();
    private Map<Integer, NlpBaseElement> arguments = new HashMap<>();

    public Relation() {
        id = "";
    }

    public Relation(String id) {
        this.setId(id);
    }

    public boolean containsProperty(String name) {
        return properties.containsKey(name);
    }

    public String getProperty(String name) {
        if (properties.containsKey(name))
            return properties.get(name);
        return null;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getArgumentsCount() {
        return argumentIds.size();
    }

    public void setArgumentId(int index, String argumentId) {
        argumentIds.put(index, argumentId);
    }

    public String getArgumentId(int index) {
        if (!argumentIds.containsKey(index))
            return null;
        return argumentIds.get(index);
    }

    public void setArgument(int index, NlpBaseElement argument) {
        arguments.put(index, argument);
    }

    public NlpBaseElement getArgument(int index) {
        if (!arguments.containsKey(index))
            return null;
        return arguments.get(index);
    }
    public Collection<String> getArgumentIds() {
        return argumentIds.values();
    }

    public Collection<NlpBaseElement> getArguments() {
        return arguments.values();
    }

    public boolean hasSameArguments(Relation r) {
        if (r == null)
            return getArgumentsCount() > 0;
        if(r.getArgumentsCount() != r.getArgumentsCount())
            return  false;
        for(int i=0; i< getArgumentsCount(); i++){
            if(getArgumentId(i) != r.getArgumentId(i))
                return false;
        }
        return true;
    }

    public NlpBaseElement getParent() {
        return parent;
    }

    public void setParent(NlpBaseElement parent) {
        this.parent = parent;
    }
}
