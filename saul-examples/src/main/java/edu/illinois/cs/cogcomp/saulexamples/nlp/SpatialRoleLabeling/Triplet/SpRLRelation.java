/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Triplet;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.RelationEval;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLSentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taher on 8/10/16.
 */
public class SpRLRelation {
    private final SpRole trajector;
    private final SpRole spatialIndicator;
    private final SpRole landmark;
    private final SpRLLabels relationLabel;
    private final SpRLLabels spLabel;
    private final SpRLLabels trLabel;
    private final SpRLLabels lmLabel;
    private final Sentence sentence;
    private final List<SpRole> orderedArgs;
    private final String relationId;
    private final IntPair sentenceOffset;


    public SpRLRelation(SpRLSentence sentence, IntPair trajectorSpan, IntPair spatialIndicatorSpan,
                        IntPair landmarkSpan, SpRLLabels relationLabel, SpRLLabels spLabel, SpRLLabels trLabel, SpRLLabels lmLabel, String relationId) {

        this.trajector = new SpRole(trajectorSpan, SpRoleTypes.TRAJECTOR, sentence.getSentence());
        this.spatialIndicator = new SpRole(spatialIndicatorSpan, SpRoleTypes.INDICATOR, sentence.getSentence());
        this.landmark = landmarkSpan == null ? null :
                new SpRole(landmarkSpan, SpRoleTypes.LANDMARK, sentence.getSentence());
        this.relationLabel = relationLabel;
        this.spLabel = spLabel;
        this.trLabel = trLabel;
        this.lmLabel = lmLabel;
        this.sentence = sentence.getSentence();
        this.sentenceOffset = sentence.getOffset();

        orderedArgs = new ArrayList<SpRole>();
        getOrderedArgs().add(this.getTrajector());
        getOrderedArgs().add(this.getSpatialIndicator());
        if (landmarkIsDefined())
            getOrderedArgs().add(this.getLandmark());

        this.relationId = relationId;

        getOrderedArgs().sort(null);
    }

    public RelationEval getRelationEval(){
        int offset = sentenceOffset.getFirst();
        int lmStart = landmarkIsDefined()? getLandmark().getFirstConstituent().getStartCharOffset() + offset: -1;
        int lmEnd = landmarkIsDefined()? getLandmark().getFirstConstituent().getEndCharOffset() + offset: -1;
        int trStart = getTrajector().getFirstConstituent().getStartCharOffset() + offset;
        int trEnd = getTrajector().getFirstConstituent().getEndCharOffset() + offset;
        int spStart = getSpatialIndicator().getFirstConstituent().getStartCharOffset() + offset;
        int spEnd = getSpatialIndicator().getFirstConstituent().getEndCharOffset() + offset;

        return new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd);
    }

    public boolean landmarkIsDefined() {
        return getLandmark() != null;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public TextAnnotation getTextAnnotation() {
        return getSentence().getSentenceConstituent().getTextAnnotation();
    }

    public SpRole getFirstArg() {
        return getOrderedArgs().get(0);
    }

    public SpRole getMidArg() {
        return getOrderedArgs().get(getOrderedArgs().size() / 2);
    }

    public SpRole getLastArg() {
        return getOrderedArgs().get(getOrderedArgs().size() - 1);
    }

    public SpRole getTrajector() {
        return trajector;
    }

    public SpRole getSpatialIndicator() {
        return spatialIndicator;
    }

    public SpRole getLandmark() {
        return landmark;
    }

    public List<SpRole> getOrderedArgs() {
        return orderedArgs;
    }

    public boolean isInArgs(IntPair p) {
        for (SpRole e : orderedArgs)
            if (e.getSpan().getFirst() <= p.getFirst() && p.getSecond() <= e.getSpan().getSecond())
                return true;
        return false;
    }

    public SpRole getCoveringArg(IntPair p) {
        for (SpRole e : orderedArgs)
            if (e.getSpan().getFirst() <= p.getFirst() && p.getSecond() <= e.getSpan().getSecond())
                return e;
        return null;
    }

    public String getRelationType() {
        List<String> types = new ArrayList<>();
        for (SpRole a : orderedArgs)
            types.add(a.getSpRoleType().toString());
        return String.join("_", types);
    }

    @Override
    public String toString() {
        List<String> texts = new ArrayList<>();
        for (SpRole a : orderedArgs)
            texts.add(a.getText() + "[" + a.getSpRoleType().toString() + "]");
        return String.join(" ", texts) + ": " + getRelationLabel();
    }

    public String getRelationId() {
        return relationId;
    }

    public SpRLLabels getRelationLabel() {
        return relationLabel;
    }

    public SpRLLabels getSpLabel() {
        return spLabel;
    }

    public SpRLLabels getTrLabel() {
        return trLabel;
    }

    public SpRLLabels getLmLabel() {
        return lmLabel;
    }

    public IntPair getSentenceOffset() {
        return sentenceOffset;
    }
}
