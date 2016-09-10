/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

import java.util.List;

/**
 * Created by taher on 8/11/16.
 */
public class SpRole implements Comparable<SpRole> {

    private final SpRoleTypes spRoleType;
    private final IntPair span;
    private final Constituent sentence;

    public SpRole(IntPair span, SpRoleTypes spRoleType, Sentence sentence) {
        this.span = span;
        this.sentence = sentence.getSentenceConstituent();
        this.spRoleType = spRoleType;
    }

    @Override
    public int compareTo(SpRole o) {
        if (getSpan().getFirst() == o.getSpan().getFirst())
            return 0;
        else if (getSpan().getFirst() < o.getSpan().getFirst())
            return -1;
        else
            return 1;
    }

    public TextAnnotation getTextAnnotation() {
        return sentence.getTextAnnotation();
    }

    public String getText() {
        return getSpan() != null ?
                String.join("_", getTextAnnotation().getTokensInSpan(getSpan().getFirst(),
                        getSpan().getSecond())).toLowerCase() :
                "[undefined]";
    }

    public Constituent getFirstConstituent() {
        if (span == null)
            return null;
        return getTextAnnotation().getView(ViewNames.TOKENS)
                .getConstituentsCoveringSpan(span.getFirst(), span.getSecond()).get(0);
    }

    public Constituent getLastConstituent() {
        if (span == null)
            return null;
        List<Constituent> c = getTextAnnotation().getView(ViewNames.TOKENS)
                .getConstituentsCoveringSpan(span.getFirst(), span.getSecond());
        return c.get(c.size() - 1);
    }

    public IntPair getSpan() {
        return span;
    }

    public boolean isCovering(IntPair s){
        return s!= null && span.getFirst() <= s.getFirst() && s.getSecond() <= span.getSecond();
    }

    public SpRoleTypes getSpRoleType() {
        return spRoleType;
    }
}
