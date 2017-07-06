/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.ISpanElement;

/**
 * Created by Taher on 2016-12-28.
 */
public interface ISpanElementMatching {
    boolean matches(ISpanElement xmlElement, ISpanElement element);
}
