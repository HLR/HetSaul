package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

import java.util.List;

/**
 * Created by taher on 8/11/16.
 */
public class RobertsElement implements Comparable<RobertsElement> {

    private final RobertsElementTypes elementType;
    private final IntPair span;
    private final Constituent sentence;

    public RobertsElement(IntPair span, RobertsElementTypes elementType, Sentence sentence) {
        this.span = span;
        this.sentence = sentence.getSentenceConstituent();
        this.elementType = elementType;
    }

    @Override
    public int compareTo(RobertsElement o) {
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

    public RobertsElementTypes getElementType() {
        return elementType;
    }
}
