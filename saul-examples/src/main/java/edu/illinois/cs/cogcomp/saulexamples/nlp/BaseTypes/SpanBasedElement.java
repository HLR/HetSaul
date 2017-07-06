/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-28.
 */
public class SpanBasedElement implements ISpanElement {

    private int start;
    private int end;
    private String text;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean matches(ISpanElement e) {
        if (e == null)
            return false;
        return getStart() == e.getStart() &&
                getEnd() == e.getEnd();
    }

    @Override
    public boolean contains(ISpanElement e) {
        if (e == null)
            return false;
        return matches(e) ||
                (getStart() <= e.getStart() && getEnd() >= e.getEnd());
    }

    @Override
    public boolean isPartOf(ISpanElement e) {
        if (e == null)
            return false;
        return e.contains(this);
    }

    @Override
    public boolean overlaps(ISpanElement e) {
        if (e == null)
            return false;
        return matches(e) ||
                (getStart() <= e.getStart() && e.getStart() < getEnd()) ||
                (e.getStart() <= getStart() && getStart() < e.getEnd());
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
