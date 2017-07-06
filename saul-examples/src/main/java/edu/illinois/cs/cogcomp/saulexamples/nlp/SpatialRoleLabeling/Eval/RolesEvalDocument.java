/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-09-19.
 */
@XmlRootElement(name = "Roles")
public class RolesEvalDocument {

    public RolesEvalDocument(List<RoleEval> trajectors, List<RoleEval> spatialIndicators, List<RoleEval> landmarks) {
        Trajectors = trajectors;
        Landmarks = landmarks;
        SpatialIndicators = spatialIndicators;
    }
    public RolesEvalDocument() {
        Trajectors = new ArrayList<>();
        Landmarks = new ArrayList<>();
        SpatialIndicators = new ArrayList<>();
    }

    @XmlElement(name = "Trajector", required = true)
    protected List<RoleEval> Trajectors;

    @XmlElement(name = "Landmark", required = true)
    protected List<RoleEval> Landmarks;

    @XmlElement(name = "SpatialIndicator", required = true)
    protected List<RoleEval> SpatialIndicators;

    public List<RoleEval> getTrajectors() {
        return Trajectors;
    }

    public List<RoleEval> getLandmarks() {
        return Landmarks;
    }

    public List<RoleEval> getSpatialIndicators() {
        return SpatialIndicators;
    }
}
