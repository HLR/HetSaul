package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

/**
 * Created by taher on 2017-04-18.
 */
public interface RelationTypeExtractor {
    String getType(RelationEval e, boolean isActual);
}
