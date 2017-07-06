package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import javax.xml.bind.annotation.*;

/**
 * Created by Taher on 2016-09-19.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Relation")
public class RelationEval implements SpRLEval {

    private final RoleEval tr;
    private final RoleEval sp;
    private final RoleEval lm;
    private String generalType;
    private String SpecificType;
    private String RCC8;
    private String FoR;

    public RelationEval() {
        this(-1, -1, -1, -1, -1, -1);
    }

    public RelationEval(int trajectorStart, int trajectorEnd, int spatialIndicatorStart, int spatialIndicatorEnd, int landmarkStart, int landmarkEnd) {
        this.trajectorStart = trajectorStart;
        this.trajectorEnd = trajectorEnd;
        this.landmarkStart = landmarkStart;
        this.landmarkEnd = landmarkEnd;
        this.spatialIndicatorStart = spatialIndicatorStart;
        this.spatialIndicatorEnd = spatialIndicatorEnd;
        tr = new RoleEval(trajectorStart, trajectorEnd);
        lm = new RoleEval(landmarkStart, landmarkEnd);
        sp = new RoleEval(spatialIndicatorStart, spatialIndicatorEnd);
    }

    @XmlAttribute(name = "trajectorStart", required = true)
    private int trajectorStart;
    @XmlAttribute(name = "trajectorEnd", required = true)
    private int trajectorEnd;

    @XmlAttribute(name = "landmarkStart", required = true)
    private int landmarkStart;
    @XmlAttribute(name = "landmarkEnd", required = true)
    private int landmarkEnd;

    @XmlAttribute(name = "spatialIndicatorStart", required = true)
    private int spatialIndicatorStart;
    @XmlAttribute(name = "spatialIndicatorEnd", required = true)
    private int spatialIndicatorEnd;

    public int getTrajectorStart() {
        return trajectorStart;
    }

    public void setTrajectorStart(int trajectorStart) {
        this.trajectorStart = trajectorStart;
    }

    public int getTrajectorEnd() {
        return trajectorEnd;
    }

    public void setTrajectorEnd(int trajectorEnd) {
        this.trajectorEnd = trajectorEnd;
    }

    public int getLandmarkStart() {
        return landmarkStart;
    }

    public void setLandmarkStart(int landmarkStart) {
        this.landmarkStart = landmarkStart;
    }

    public int getLandmarkEnd() {
        return landmarkEnd;
    }

    public void setLandmarkEnd(int landmarkEnd) {
        this.landmarkEnd = landmarkEnd;
    }

    public int getSpatialIndicatorStart() {
        return spatialIndicatorStart;
    }

    public void setSpatialIndicatorStart(int spatialIndicatorStart) {
        this.spatialIndicatorStart = spatialIndicatorStart;
    }

    public int getSpatialIndicatorEnd() {
        return spatialIndicatorEnd;
    }

    public void setSpatialIndicatorEnd(int spatialIndicatorEnd) {
        this.spatialIndicatorEnd = spatialIndicatorEnd;
    }

    @Override
    public boolean isEqual(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RelationEval obj = (RelationEval) b;
        return sp.isEqual(obj.sp) && tr.isEqual(obj.tr) && lm.isEqual(obj.lm);
    }

    @Override
    public boolean overlaps(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RelationEval obj = (RelationEval) b;
        return sp.overlaps(obj.sp) && tr.overlaps(obj.tr) && lm.overlaps(obj.lm);
    }

    @Override
    public boolean contains(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RelationEval obj = (RelationEval) b;
        return sp.contains(obj.sp) && tr.contains(obj.tr) && lm.contains(obj.lm);
    }

    @Override
    public boolean isPartOf(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RelationEval obj = (RelationEval) b;
        return sp.isPartOf(obj.sp) && tr.isPartOf(obj.tr) && lm.isPartOf(obj.lm);
    }

    @Override
    public int hashCode() {
        return (getHashCode(lm) + "-" + getHashCode(sp) + "-" + getHashCode(tr)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        return hashCode() == obj.hashCode();
    }

    private int getHashCode(RoleEval r) {
        return r == null ? new RoleEval().hashCode() : r.hashCode();
    }

    public String getGeneralType() {
        return generalType;
    }

    public void setGeneralType(String generalType) {
        this.generalType = generalType;
    }

    public String getSpecificType() {
        return SpecificType;
    }

    public void setSpecificType(String specificType) {
        SpecificType = specificType;
    }

    public String getRCC8() {
        return RCC8;
    }

    public void setRCC8(String RCC8) {
        this.RCC8 = RCC8;
    }

    public String getFoR() {
        return FoR;
    }

    public void setFoR(String foR) {
        FoR = foR;
    }
}
