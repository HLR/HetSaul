/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

/**
 * Created by Taher on 2016-09-20.
 */
public interface SpRLEval {
    boolean isEqual(SpRLEval b);

    boolean overlaps(SpRLEval b);

    boolean contains(SpRLEval b);

    boolean isPartOf(SpRLEval b);
}
