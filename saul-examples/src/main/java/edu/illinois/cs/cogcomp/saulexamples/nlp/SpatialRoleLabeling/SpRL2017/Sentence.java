/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SENTENCE")
public class Sentence {

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "start", required = true)
    private int start;

    @XmlAttribute(name = "end", required = true)
    private int end;

    @XmlElement(name = "TEXT", required = true)
    private String text;

    @XmlElement(name = "SPATIALINDICATOR", required = true)
    private List<SPATIALINDICATOR> spatialindicators;

    @XmlElement(name = "LANDMARK", required = true)
    private List<LANDMARK> landmarks;

    @XmlElement(name = "TRAJECTOR", required = true)
    private List<TRAJECTOR> trajectors;

    @XmlElement(name = "RELATION", required = true)
    private List<RELATION> relations;

    public Sentence() {
        this.spatialindicators = new ArrayList<>();
        this.landmarks = new ArrayList<>();
        this.trajectors = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SPATIALINDICATOR> getSpatialindicators() {
        return spatialindicators;
    }

    public void setSpatialindicators(List<SPATIALINDICATOR> spatialindicators) {
        this.spatialindicators = spatialindicators;
    }

    public List<LANDMARK> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<LANDMARK> landmarks) {
        this.landmarks = landmarks;
    }

    public List<TRAJECTOR> getTrajectors() {
        return trajectors;
    }

    public void setTrajectors(List<TRAJECTOR> trajectors) {
        this.trajectors = trajectors;
    }

    public List<RELATION> getRelations() {
        return relations;
    }

    public void setRelations(List<RELATION> relations) {
        this.relations = relations;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
