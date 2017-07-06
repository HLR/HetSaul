/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlRootElement(name = "SCENE")
public class Scene {
    private String docNo;
    private String image;
    private List<Sentence> sentences;

    public Scene() {
        this.sentences = new ArrayList<>();
    }

    @XmlElement(name = "DOCNO", required = true)
    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    @XmlElement(name = "IMAGE", required = true)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @XmlElement(name = "SENTENCE", required = true)
    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

}
