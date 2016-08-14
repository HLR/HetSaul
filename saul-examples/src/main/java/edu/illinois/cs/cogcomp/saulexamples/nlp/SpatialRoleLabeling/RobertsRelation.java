/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.RobertsElement;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.RobertsElementTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taher on 8/10/16.
 */
public class RobertsRelation {
    private final RobertsElement trajector;
    private final RobertsElement spatialIndicator;
    private final RobertsElement landmark;
    private final RobertsRelationLabels label;
    private final Sentence sentence;
    private final List<RobertsElement> orderedArgs;


    public RobertsRelation(Sentence sentence, IntPair trajectorSpan, IntPair spatialIndicatorSpan,
                           IntPair landmarkSpan, RobertsRelationLabels label) {

        this.trajector = new RobertsElement(trajectorSpan, RobertsElementTypes.TRAJECTOR, sentence);
        this.spatialIndicator = new RobertsElement(spatialIndicatorSpan, RobertsElementTypes.INDICATOR, sentence);
        this.landmark = landmarkSpan == null ? null :
                new RobertsElement(landmarkSpan, RobertsElementTypes.LANDMARK, sentence);
        this.label = label;
        this.sentence = sentence;

        orderedArgs = new ArrayList<RobertsElement>();
        getOrderedArgs().add(this.getTrajector());
        getOrderedArgs().add(this.getSpatialIndicator());
        if (landmarkIsDefined())
            getOrderedArgs().add(this.getLandmark());

        getOrderedArgs().sort(null);
    }

    public boolean landmarkIsDefined() {
        return getLandmark() != null;
    }

    public RobertsRelationLabels getLabel() {
        return label;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public TextAnnotation getTextAnnotation() {
        return getSentence().getSentenceConstituent().getTextAnnotation();
    }

    public RobertsElement getFirstArg() {
        return getOrderedArgs().get(0);
    }

    public RobertsElement getMidArg() {
        return getOrderedArgs().get(getOrderedArgs().size() / 2);
    }

    public RobertsElement getLastArg() {
        return getOrderedArgs().get(getOrderedArgs().size() - 1);
    }

    public RobertsElement getTrajector() {
        return trajector;
    }

    public RobertsElement getSpatialIndicator() {
        return spatialIndicator;
    }

    public RobertsElement getLandmark() {
        return landmark;
    }

    public List<RobertsElement> getOrderedArgs() {
        return orderedArgs;
    }

    public boolean isInArgs(IntPair p) {
        for (RobertsElement e : orderedArgs)
            if (e.getSpan().getFirst() <= p.getFirst() && p.getSecond() <= e.getSpan().getSecond())
                return true;
        return false;
    }

    public RobertsElement getCoveringArg(IntPair p) {
        for (RobertsElement e : orderedArgs)
            if (e.getSpan().getFirst() <= p.getFirst() && p.getSecond() <= e.getSpan().getSecond())
                return e;
        return null;
    }

    public String getRelationType() {
        List<String> types = new ArrayList<>();
        for (RobertsElement a : orderedArgs)
            types.add(a.getElementType().toString());
        return String.join("_", types);
    }

    @Override
    public String toString() {
        List<String> texts = new ArrayList<>();
        for (RobertsElement a : orderedArgs)
            texts.add(a.getText() + "[" + a.getElementType().toString() + "]");
        return String.join(" ", texts) + ": " + getLabel();
    }

    public enum RobertsRelationLabels {
        GOLD,
        CANDIDATE,
    }

}
