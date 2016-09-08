package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

/**
 * Created by Taher on 2016-09-08.
 */
public class SpRLRelation {
    private final SpRLAnnotation spatialIndicator;
    private final SpRLAnnotation trajector;
    private final SpRLAnnotation landmark;
    private final String id;

    public SpRLRelation(String id, SpRLAnnotation spatialIndicator, SpRLAnnotation trajector, SpRLAnnotation landmark) {
        this.spatialIndicator = spatialIndicator;
        this.trajector = trajector;
        this.landmark = landmark;
        this.id = id;
    }

    public SpRLAnnotation getSpatialIndicator() {
        return spatialIndicator;
    }

    public SpRLAnnotation getTrajector() {
        return trajector;
    }

    public SpRLAnnotation getLandmark() {
        return landmark;
    }

    public String getId() {
        return id;
    }

}
