/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-28.
 */
public interface SpanBasedElement {
    int getStart();
    void setStart(int start);
    void setEnd(int end);
    int getEnd();
    String getText();
    boolean matches(SpanBasedElement e);
    boolean contains(SpanBasedElement e);
    boolean isPartOf(SpanBasedElement e);
    boolean overlaps(SpanBasedElement e);
}
