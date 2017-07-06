package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import javax.xml.bind.annotation.*;

/**
 * Created by Taher on 2016-09-19.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Role")
public class RoleEval implements SpRLEval {

    public RoleEval() {
        setEnd(-1);
        setStart(-1);
    }

    public RoleEval(int start, int end) {
        this.setStart(start);
        this.setEnd(end);
    }

    @XmlAttribute(name = "start", required = true)
    private int start;
    @XmlAttribute(name = "end", required = true)
    private int end;

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
    public boolean isEqual(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RoleEval obj = (RoleEval) b;
        return getStart() == obj.getStart() && getEnd() == obj.getEnd();
    }

    @Override
    public boolean overlaps(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RoleEval obj = (RoleEval) b;
        return isEqual(b) || (getStart() <= obj.getStart() && obj.getStart() < getEnd()) ||
                (obj.getStart() <= getStart() && getStart() < obj.getEnd());
    }

    @Override
    public boolean contains(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RoleEval obj = (RoleEval) b;
        return isEqual(b) || (getStart() <= obj.getStart() && obj.getEnd() <= getEnd());
    }

    @Override
    public boolean isPartOf(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RoleEval obj = (RoleEval) b;
        return isEqual(b) || (obj.getStart() <= getStart() && getEnd() <= obj.getEnd());
    }

    @Override
    public int hashCode() {
        return (getStart() + "-" + getEnd()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        return hashCode() == obj.hashCode();
    }
}
