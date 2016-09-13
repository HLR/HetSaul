/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;

import java.util.List;

/**
 * Created by Taher on 2016-09-06.
 */
public class SpRLSentence {
    private final IntPair offset;
    private final Sentence sentence;
    private final List<SpRLRelation> relations;
    public SpRLSentence(IntPair offset, Sentence sentence, List<SpRLRelation> relations) {
        this.offset = offset;
        this.sentence = sentence;
        this.relations = relations;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public List<SpRLRelation> getRelations() {
        return relations;
    }

    public IntPair getOffset() {
        return offset;
    }
}
