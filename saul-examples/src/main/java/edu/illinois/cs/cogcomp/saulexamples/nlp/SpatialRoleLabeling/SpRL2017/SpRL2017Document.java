/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.LANDMARK;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SPATIALINDICATOR;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.TRAJECTOR;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLXmlDocument;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SpRL")
public class SpRL2017Document implements SpRLXmlDocument {
    @XmlElement(name = "SCENE", required = true)
    private List<Scene> Scenes;

    public SpRL2017Document() {
        Scenes = new ArrayList<>();
    }

    public List<Scene> getScenes() {
        return Scenes;
    }

    public void setScenes(List<Scene> scenes) {
        Scenes = scenes;
    }

    @XmlTransient
    protected String filename;
    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlTransient
    private Map<String, LANDMARK> idToLandmark = null;
    @XmlTransient
    private Map<String, SPATIALINDICATOR> idToIndicator = null;
    @XmlTransient
    private Map<String, TRAJECTOR> idToTrajector = null;
    @XmlTransient
    private Map<String, Sentence> roleIdToSentence = null;

    public LANDMARK getLandmark(String id) {
        if (idToLandmark == null)
            buildMaps();
        if (idToLandmark.containsKey(id))
            return idToLandmark.get(id);
        return null;
    }

    public SPATIALINDICATOR getIndicator(String id) {
        if (idToIndicator == null)
            buildMaps();
        if (idToIndicator.containsKey(id))
            return idToIndicator.get(id);
        return null;
    }

    public TRAJECTOR getTrajector(String id) {
        if (idToTrajector == null)
            buildMaps();
        if (idToTrajector.containsKey(id))
            return idToTrajector.get(id);
        return null;
    }

    public List<LANDMARK> getAllLandmarks() {
        if (idToLandmark == null) {
            buildMaps();
        }
        return new ArrayList<>(idToLandmark.values());
    }

    public List<SPATIALINDICATOR> getAllIndicators() {
        if (idToIndicator == null) {
            buildMaps();
        }
        return new ArrayList<>(idToIndicator.values());
    }

    public List<TRAJECTOR> getAllTrajectors() {
        if (idToTrajector == null) {
            buildMaps();
        }
        return new ArrayList<>(idToTrajector.values());
    }

    public Sentence getRoleSentence(String id) {
        if (roleIdToSentence == null) {
            buildMaps();
        }
        if (roleIdToSentence.containsKey(id))
            return roleIdToSentence.get(id);
        return null;
    }

    private void buildMaps() {
        idToLandmark = new HashMap<>();
        idToIndicator = new HashMap<>();
        idToTrajector = new HashMap<>();
        roleIdToSentence = new HashMap<>();
        for (Scene s : getScenes()) {
            for (Sentence sentence : s.getSentences()) {
                for (LANDMARK l : sentence.getLandmarks()) {
                    if (l.getStart() > -1 && l.getEnd() > -1) {
                        idToLandmark.put(l.getId(), l);
                        roleIdToSentence.put(l.getId(), sentence);
                    }
                }
                for (SPATIALINDICATOR sp : sentence.getSpatialindicators()) {
                    if (sp.getStart() > -1 && sp.getEnd() > -1) {
                        idToIndicator.put(sp.getId(), sp);
                        roleIdToSentence.put(sp.getId(), sentence);
                    }
                }
                for (TRAJECTOR t : sentence.getTrajectors()) {
                    if (t.getStart() > -1 && t.getEnd() > -1) {
                        idToTrajector.put(t.getId(), t);
                        roleIdToSentence.put(t.getId(), sentence);
                    }
                }
            }
        }
    }

}
