/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.TEXT;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlRootElement(name = "DOC")
public class ClefDocument implements SpRLXmlDocument{

    private String docNo;
    private String description;
    private String image;

    @XmlTransient
    protected String filename;

    @XmlElement(name = "DOCNO", required = true)
    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    @XmlElement(name = "DESCRIPTION", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "IMAGE", required = true)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
