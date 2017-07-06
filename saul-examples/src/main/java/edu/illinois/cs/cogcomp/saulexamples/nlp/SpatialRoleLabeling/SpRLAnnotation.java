/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import java.math.BigInteger;

/**
 * Created by taher on 7/30/16.
 */
public interface SpRLAnnotation {
    Integer getStart();
    Integer getEnd();
    void setStart(Integer x);
    void setEnd(Integer x);
    String getText();
    void setText(String text);
    String getId();
    void setId(String id);
}
