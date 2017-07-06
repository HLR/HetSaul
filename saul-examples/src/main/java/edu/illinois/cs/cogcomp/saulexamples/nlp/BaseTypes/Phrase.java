/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-24.
 */
public class Phrase extends NlpBaseElement {

    private Sentence sentence;

    public Phrase(){

    }

    public Phrase(Sentence sentence, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.sentence = sentence;
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Phrase;
    }

    public Document getDocument() {
        return getSentence().getDocument();
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }
}