/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.*;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.*;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 * Created by taher on 7/25/16.
 */
public class SpRLDocumentReaderTest {
    private SpRLDataReader<SpRL2015Document> sprl2015Reader;
    private SpRLDataReader<SpRL2013Document> sprl2013Reader;

    @Before
    public void setup() throws ParserConfigurationException, JAXBException, SAXException, IOException {

        sprl2015Reader = new SpRLDataReader(getResourcePath("SpRL/2015"), SpRL2015Document.class);
        sprl2013Reader = new SpRLDataReader(getResourcePath("SpRL/2013"), SpRL2013Document.class);

        sprl2015Reader.readData();
        sprl2013Reader.readData();
    }
    private String getResourcePath(String relativePath){
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
    @Test
    public void sprl2015DocumentCount() {
        assertEquals("SpRL2013Document 2015 Document count", sprl2015Reader.documents.size(), 1);
    }
    @Test
    public void sprl2015TagsCount() {
        int count = sprl2015Reader.documents.get(0).getTAGS().getPLACE().size();
        assertEquals("place count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getPATH().size();
        assertEquals("path count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getSPATIALENTITY().size();
        assertEquals("Spatial entity count", count, 2);

        count = sprl2015Reader.documents.get(0).getTAGS().getNONMOTIONEVENT().size();
        assertEquals("non-motion event count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMOTION().size();
        assertEquals("motion count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getSPATIALSIGNAL().size();
        assertEquals("spatial signal count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMOTIONSIGNAL().size();
        assertEquals("motion signal count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMEASURE().size();
        assertEquals("meaure count", count, 2);

        count = sprl2015Reader.documents.get(0).getTAGS().getQSLINK().size();
        assertEquals("QSLink count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getOLINK().size();
        assertEquals("OLink count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMOVELINK().size();
        assertEquals("move link count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMEASURELINK().size();
        assertEquals("measure link count", count, 1);

        count = sprl2015Reader.documents.get(0).getTAGS().getMETALINK().size();
        assertEquals("metaLink count", count, 1);
    }
    @Test
    public void sprl2015SpatialEntity() {
        SPATIALENTITY sp = sprl2015Reader.documents.get(0).getTAGS().getSPATIALENTITY().get(0);
        assertEquals(sp.getComment(), "subsuming mover for 'Daniel and I start with our Enduros'" );
        assertEquals(sp.getCountable(), "TRUE");
        assertEquals(sp.getDcl(), "FALSE");
        assertEquals(sp.getDimensionality(), "POINT");
        assertEquals(sp.getDomain(), "");
        assertEquals(sp.getElevation(),"");
        assertEquals(sp.getEnd().intValue(), -1);
        assertEquals(sp.getStart().intValue(), -1);
        assertEquals(sp.getForm(), "NOM");
        assertEquals(sp.getId(), "se21");
        assertEquals(sp.getText(), "");

    }
    @Test
    public void sprl2015Text(){
        String text = sprl2015Reader.documents.get(0).getTEXT();
        assertEquals(text, "test document");
    }


    @Test
    public void sprl2013DocumentCount() {
        assertEquals("SpRL2013Document 2013 Document count", sprl2013Reader.documents.size(), 1);
    }
    @Test
    public void sprl2013TagsCount() {
        int count = sprl2013Reader.documents.get(0).getTAGS().getDIRECTION().size();
        assertEquals("direction count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getPATH().size();
        assertEquals("path count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getDISTANCE().size();
        assertEquals("distance count", count, 0);

        count = sprl2013Reader.documents.get(0).getTAGS().getLANDMARK().size();
        assertEquals("landmark count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getMOTIONINDICATOR().size();
        assertEquals("motion indicator count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getRELATION().size();
        assertEquals("relation count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getSPATIALINDICATOR().size();
        assertEquals("spatial indicator count", count, 1);

        count = sprl2013Reader.documents.get(0).getTAGS().getTRAJECTOR().size();
        assertEquals("trajector count", count, 2);

    }
    @Test
    public void sprl2013SpatialIndicator() {
        SPATIALINDICATOR sp = sprl2013Reader.documents.get(0).getTAGS().getSPATIALINDICATOR().get(0);
        assertEquals(sp.getEnd().intValue(), 409);
        assertEquals(sp.getText(), "in the middle of");
        assertEquals(sp.getId(), "S0");
        assertEquals(sp.getStart().intValue(), 393);
    }
    @Test
    public void sprl2013Text(){
        String text = sprl2013Reader.documents.get(0).getTEXT().getContent();
        assertEquals(text, "test document");
    }
}
