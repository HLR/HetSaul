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
@XmlRootElement(name = "Relations")
public class RelationsEvalDocument {

    public RelationsEvalDocument(List<RelationEval> relations) {
        Relations = relations;
    }

    public RelationsEvalDocument() {
        Relations = new ArrayList<>();
    }

    @XmlElement(name = "Relation", required = true)
    private List<RelationEval> Relations;

    public List<RelationEval> getRelations() {
        return Relations;
    }
}
