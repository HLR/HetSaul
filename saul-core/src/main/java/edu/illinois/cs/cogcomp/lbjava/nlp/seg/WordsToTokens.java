package edu.illinois.cs.cogcomp.lbjava.nlp.seg;

import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;


/**
  * This parser calls another parser that returns {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector}s of
  * {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s, converts the {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s to {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s, and returns
  * {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector}s of {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s.
  *
  * @author Nick Rizzolo
 **/
public class WordsToTokens implements Parser
{
  /** A parser that returns {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector}s of {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s. */
  protected Parser parser;


  /**
    * Creates the parser.
    *
    * @param p  A parser that returns {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector}s of {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s.
   **/
  public WordsToTokens(Parser p) { parser = p; }


  /**
    * Returns the next {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} of {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s.
    *
    * @return The next {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} of {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s parsed, or
    *         <code>null</code> if there are no more children in the stream.
   **/
  public Object next() {
    return convert((LinkedVector) parser.next());
  }


  /**
    * Given a {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} containing {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s, this method
    * creates a new {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} containing {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s.
    *
    * @param v  A {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} of {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s.
    * @return A {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector} of {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s corresponding to the
    *         input {@link edu.illinois.cs.cogcomp.lbjava.nlp.Word}s.
   **/
  public static LinkedVector convert(LinkedVector v) {
    if (v == null) return null;
    if (v.size() == 0) return v;

    Word w = (Word) v.get(0);
    Token t = new Token(w, null, null);
    for (w = (Word) w.next; w != null; w = (Word) w.next) {
      t.next = new Token(w, t, null);
      t = (Token) t.next;
    }

    return new LinkedVector(t);
  }


  /** Sets this parser back to the beginning of the raw data. */
  public void reset() { parser.reset(); }


  /** Frees any resources this parser may be holding. */
  public void close() { parser.close(); }
}

