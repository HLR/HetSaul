/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.vision;

/**
 * Created by Umar Manzoor on 02/01/2017.
 */
public class SegmentRelation {
    private String imageId;
    private int firstSegmentId;
    private int secondSegmentId;
    private String relation;

    public SegmentRelation(String imageId, int firstSegmentId, int secondSegmentId, String relation) {
        this.imageId = imageId;
        this.firstSegmentId = firstSegmentId;
        this.secondSegmentId = secondSegmentId;
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public int getFirstSegmentId() {
        return firstSegmentId;
    }

    public int getSecondSegmentId() {
        return secondSegmentId;
    }
    public String getImageId()
    {
        return imageId;
    }

}
