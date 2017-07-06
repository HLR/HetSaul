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
public class Token extends NlpBaseElement {

    private Sentence sentence;
    private Phrase phrase;

    public Token() {

    }

    public Token(Sentence sentence, String id, Integer start, Integer end, String text) {
        this(sentence, null, id, start, end, text);
    }

    public Token(Phrase phrase, String id, Integer start, Integer end, String text) {
        this(phrase.getSentence(), phrase, id, start, end, text);
    }

    public Token(Sentence sentence, Phrase phrase, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.setSentence(sentence);
        this.setPhrase(phrase);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Token;
    }

    public Document getDocument() {
        return getSentence().getDocument();
    }

    public Sentence getSentence() {
        if (sentence != null)
            return sentence;
        return phrase != null ? phrase.getSentence() : null;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.phrase = phrase;
    }
}
