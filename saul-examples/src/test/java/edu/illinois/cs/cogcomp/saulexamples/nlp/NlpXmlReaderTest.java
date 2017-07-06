package edu.illinois.cs.cogcomp.saulexamples.nlp;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.*;
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReaderTest {
    NlpXmlReader reader;
    private List<Document> documents;

    @Before
    public void setup() {
        reader = new NlpXmlReader(getResourcePath("SpRL/2017/test.xml"), "SCENE", "SENTENCE", "TRAJECTOR", null);
        reader.setIdUsingAnotherProperty("SCENE","DOCNO");
        documents = reader.getDocuments();
    }

    @Test
    public void document() {
        assertEquals("Document count", 2, documents.size());
        assertEquals("Document 1 Id", "annotations/01/1060.eng", documents.get(0).getId());
        assertEquals("Document 2 Id", "annotations/01/1069.eng", documents.get(1).getId());
        assertEquals("Document 1 test attribute", "test", documents.get(0).getPropertyFirstValue("test"));
    }

    @Test
    public void sentence() {
        String docId = documents.get(0).getId();
        List<Sentence> sentences = reader.getSentencesByParentId(docId);
        assertEquals("Document 1 sentence count", 2, sentences.size());
        assertEquals("sentence 1 documentId", docId, sentences.get(0).getDocument().getId());
    }

    @Test
    public void phrase() {
        String docId = documents.get(0).getId();
        List<Phrase> phrases = reader.getPhrasesByParentId(docId, "TESTPROP");
        assertEquals("Document 1 Trajector phrase count", 3, phrases.size());
        assertEquals("phrase 1 documentId", docId, phrases.get(0).getDocument().getId());
        assertEquals("phrase 1 sentenceId", "s601", phrases.get(0).getSentence().getId());

        assertEquals("first phrase additional prop[first_value]", "1", phrases.get(0).getPropertyFirstValue("TESTPROP_first_value"));
        assertEquals("first phrase additional prop[second_value]", "T1", phrases.get(0).getPropertyFirstValue("TESTPROP_second_value"));
        assertEquals("second phrase additional prop[first_value]", "2", phrases.get(1).getPropertyFirstValue("TESTPROP_first_value"));
        assertEquals("second phrase additional prop[second_value]", "T2", phrases.get(1).getPropertyFirstValue("TESTPROP_second_value"));
        assertEquals("third phrase additional prop[first_value]", null, phrases.get(2).getPropertyFirstValue("TESTPROP_first_value"));
        assertEquals("third phrase additional prop[second_value]", null, phrases.get(2).getPropertyFirstValue("TESTPROP_second_value"));

    }

    @Test
    public void Overlapping(){
        reader.close();
        List<Phrase> phrases = reader.getPhrases();
        reader.addPropertiesFromTag("MATCH", phrases, new OverlapMatching());
        assertTrue("contains p1", phrases.get(3).getPropertyValues("MATCH_id").contains("p1"));
        assertTrue("contains p2", phrases.get(3).getPropertyValues("MATCH_id").contains("p2"));
        assertTrue("contains p3", phrases.get(3).getPropertyValues("MATCH_id").contains("p3"));
        assertTrue("contains o1", phrases.get(3).getPropertyValues("MATCH_id").contains("o1"));
        assertTrue("contains inc1", phrases.get(4).getPropertyValues("MATCH_id").contains("inc1"));
        assertTrue("contains inc2", phrases.get(6).getPropertyValues("MATCH_id").contains("inc2"));
        assertTrue("contains e1", phrases.get(6).getPropertyValues("MATCH_id").contains("e1"));
    }
    @Test
    public void Include(){
        reader.close();
        List<Phrase> phrases = reader.getPhrases();
        reader.addPropertiesFromTag("MATCH", phrases, new InclusionMatching());
        assertFalse("does not contain p1", phrases.get(3).getPropertyValues("MATCH_id").contains("p1"));
        assertFalse("does not contain p2", phrases.get(3).getPropertyValues("MATCH_id").contains("p2"));
        assertFalse("does not contain p3", phrases.get(3).getPropertyValues("MATCH_id").contains("p3"));
        assertFalse("does not contain o1", phrases.get(3).getPropertyValues("MATCH_id").contains("o1"));
        assertTrue("contains inc1", phrases.get(4).getPropertyValues("MATCH_id").contains("inc1"));
        assertTrue("contains inc2", phrases.get(6).getPropertyValues("MATCH_id").contains("inc2"));
        assertTrue("contains e1", phrases.get(6).getPropertyValues("MATCH_id").contains("e1"));
    }
    @Test
    public void partOf(){
        reader.close();
        List<Phrase> phrases = reader.getPhrases();
        reader.addPropertiesFromTag("MATCH", phrases, new PartOfMatching());
        assertTrue("contains p1", phrases.get(3).getPropertyValues("MATCH_id").contains("p1"));
        assertTrue("contains p2", phrases.get(3).getPropertyValues("MATCH_id").contains("p2"));
        assertTrue("contains p3", phrases.get(3).getPropertyValues("MATCH_id").contains("p3"));
        assertFalse("does not contain o1", phrases.get(3).getPropertyValues("MATCH_id").contains("o1"));
        assertFalse("does not contain inc1", phrases.get(4).getPropertyValues("MATCH_id").contains("inc1"));
        assertFalse("does not contain inc2", phrases.get(6).getPropertyValues("MATCH_id").contains("inc2"));
        assertTrue("contains e1", phrases.get(6).getPropertyValues("MATCH_id").contains("e1"));
    }
    @Test
    public void exact(){
        reader.close();
        List<Phrase> phrases = reader.getPhrases();
        reader.addPropertiesFromTag("MATCH", phrases, new ExactMatching());
        assertFalse("does not contain p1", phrases.get(3).getPropertyValues("MATCH_id").contains("p1"));
        assertFalse("does not contain p2", phrases.get(3).getPropertyValues("MATCH_id").contains("p2"));
        assertFalse("does not contain p3", phrases.get(3).getPropertyValues("MATCH_id").contains("p3"));
        assertFalse("does not contain o1", phrases.get(3).getPropertyValues("MATCH_id").contains("o1"));
        assertFalse("does not contain inc1", phrases.get(4).getPropertyValues("MATCH_id").contains("inc1"));
        assertFalse("does not contain inc2", phrases.get(6).getPropertyValues("MATCH_id").contains("inc2"));
        assertTrue("contains e1", phrases.get(6).getPropertyValues("MATCH_id").contains("e1"));
    }
    @Test
    public void relation() {
        String docId = documents.get(0).getId();
        List<Relation> relations = reader.getRelations("RELATION");
        List<Relation> doc1Relations = reader.getRelationsByParentId("RELATION", docId);

        assertEquals("Relations count", 8, relations.size());
        assertEquals("first doc relations count", 3, doc1Relations.size());
        assertEquals("first relation parent id", "s601", relations.get(0).getParent().getId());
        assertEquals("first relation trajector id", "T1", relations.get(0).getProperty("trajector_id"));
        assertEquals("first relation sparial indicator id", "S1", relations.get(0).getProperty("spatial_indicator_id"));
        assertEquals("first relation RCC8_value", "behind", relations.get(0).getProperty("RCC8_value"));
        assertEquals("fourth relation parent id", "s603", relations.get(3).getParent().getId());
    }

    private String getResourcePath(String relativePath) {
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
}
