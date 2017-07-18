/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-18.
 */
public class Document extends NlpBaseElement {
    public Document() {
    }

    public Document(String id) {
        super(id, -1, -1, "");
    }

    public Document(String id, Integer start, Integer end) {
        super(id, start, end, "");
    }

    public Document(String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Document;
    }
}
