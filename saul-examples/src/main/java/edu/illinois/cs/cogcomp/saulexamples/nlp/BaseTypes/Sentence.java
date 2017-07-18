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
public class Sentence extends NlpBaseElement {

    private Document document;

    public Sentence() {
    }

    public Sentence(Document document, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.setDocument(document);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Sentence;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
