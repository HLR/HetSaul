/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saul.util.ProgressBar;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.*;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReader {
    private Map<String, NlpBaseElement> identityMap;
    private XPath xpath = null;
    org.w3c.dom.Document xmlDocument;
    private String documentTagName;
    private String sentenceTagName;
    private String phraseTagName;
    private String tokenTagName;
    private String startTagName = "start";
    private String endTagName = "end";
    private String textTagName = "text";
    private String idTagName = "id";
    private NlpBaseElementTypes relationParent = NlpBaseElementTypes.Sentence;

    public NlpXmlReader(String path, String documentTagName, String sentenceTagName, String phraseTagName, String tokenTagName) {
        this(new File(path), documentTagName, sentenceTagName, phraseTagName, tokenTagName);
    }

    public NlpXmlReader(File file, String documentTagName, String sentenceTagName, String phraseTagName, String tokenTagName) {
        identityMap = new HashMap<>();
        this.setDocumentTagName(documentTagName);
        this.setSentenceTagName(sentenceTagName);
        this.setPhraseTagName(phraseTagName);
        this.setTokenTagName(tokenTagName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            xmlDocument = dBuilder.parse(file);
            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        identityMap.clear();
    }

    public String getDocumentTagName() {
        return documentTagName;
    }

    public void setDocumentTagName(String documentTagName) {
        this.documentTagName = documentTagName;
    }

    public String getSentenceTagName() {
        return sentenceTagName;
    }

    public void setSentenceTagName(String sentenceTagName) {
        this.sentenceTagName = sentenceTagName;
    }

    public String getPhraseTagName() {
        return phraseTagName;
    }

    public void setPhraseTagName(String phraseTagName) {
        this.phraseTagName = phraseTagName;
    }

    public String getTokenTagName() {
        return tokenTagName;
    }

    public void setTokenTagName(String tokenTagName) {
        this.tokenTagName = tokenTagName;
    }

    public String getStartTagName() {
        return startTagName;
    }

    public void setStartTagName(String startTagName) {
        this.startTagName = startTagName;
    }

    public String getEndTagName() {
        return endTagName;
    }

    public void setEndTagName(String endTagName) {
        this.endTagName = endTagName;
    }

    public String getTextTagName() {
        return textTagName;
    }

    public void setTextTagName(String textTagName) {
        this.textTagName = textTagName;
    }

    public String getIdTagName() {
        return idTagName;
    }

    public void setIdUsingAnotherProperty(String tagName, String propertyName) {
        NodeList nodes = getNodes(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                e.setAttribute("id", getAttribute(e, propertyName));
            }
        }
    }

    public List<NlpBaseElement> getTagAsNlpBaseElement(String tagName, String... addPropertiesFromTag) {
        String docTag = getDocumentTagName();
        setDocumentTagName(tagName);
        List<NlpBaseElement> result = getElementList(getDocumentTagName(), null, NlpBaseElementTypes.Document);
        setDocumentTagName(docTag);
        return result;
    }

    public List<Document> getDocuments(String... addPropertiesFromTag) {
        return getElementList(getDocumentTagName(), null, NlpBaseElementTypes.Document, addPropertiesFromTag);
    }

    public List<Sentence> getSentences(String... addPropertiesFromTag) {
        return getSentencesByParentId(null, addPropertiesFromTag);
    }

    public List<Sentence> getSentencesByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getSentenceTagName(), parentId, NlpBaseElementTypes.Sentence, addPropertiesFromTag);
    }

    public List<Phrase> getPhrases(String... addPropertiesFromTag) {
        return getPhrasesByParentId(null, addPropertiesFromTag);
    }

    public List<Phrase> getPhrasesByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getPhraseTagName(), parentId, NlpBaseElementTypes.Phrase, addPropertiesFromTag);
    }

    public List<Token> getTokens(String... addPropertiesFromTag) {
        return getTokensByParentId(null, addPropertiesFromTag);
    }

    public List<Token> getTokensByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getTokenTagName(), parentId, NlpBaseElementTypes.Token, addPropertiesFromTag);
    }

    public List<Relation> getRelations(String tagName, String... argumentIds) {
        return getRelationsByParentId(tagName, null, argumentIds);
    }

    public List<Relation> getRelationsByParentId(String tagName, String parentId, String... argumentIds) {

        NodeList nodes = parentId == null ?
                getNodes(tagName) :
                getNodes(tagName, parentId);

        List<Relation> list = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element e = (Element) nodes.item(i);
                list.add(getRelation(e, argumentIds));
            }
        }
        return list;
    }

    public <T extends NlpBaseElement> void addPropertiesFromTag(String tagName, List<T> list) {
        addPropertiesFromTag(tagName, list, new ExactMatching());
    }

    public <T extends NlpBaseElement> void addPropertiesFromTag(String tagName, List<T> list, ISpanElementMatching matching) {
        System.out.println("Adding properties from '" + tagName + "' ...");
        ProgressBar progress = new ProgressBar(list.size());
        progress.progress();
        String currentParentId = null;
        NodeList matchingNodes = null;
        List<Element> xmlElements = new ArrayList<>();
        List<SpanBasedElement> elementContainers = new ArrayList<>();
        for (T e : list) {
            String parentId = getParentId(e);
            if (parentId == null) {
                System.out.println(getParentName(e) + " id is null. This can affect the matching of the tags!");
            }
            if (currentParentId != parentId) {
                currentParentId = parentId;
                matchingNodes = getNodes(tagName, parentId);
                elementContainers.clear();
                xmlElements.clear();
                for (int j = 0; j < matchingNodes.getLength(); j++) {
                    Node n = matchingNodes.item(j);
                    if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) n;
                        elementContainers.add(getElementContainer(element));
                        xmlElements.add(element);
                    }
                }
            }
            for (int j = 0; j < elementContainers.size(); j++) {
                Element container = xmlElements.get(j);
                if (matching.matches(elementContainers.get(j), e))
                    for (int i = 0; i < container.getAttributes().getLength(); i++) {
                        e.addPropertyValue(tagName + "_" + container.getAttributes().item(i).getNodeName(),
                                container.getAttributes().item(i).getNodeValue());
                    }
            }
            progress.add(1);
        }
        progress.finish();
    }

    private <T extends NlpBaseElement> String getParentId(T e) {
        switch (e.getType()) {
            case Document:
                break;
            case Sentence:
                return ((Sentence) e).getDocument().getId();
            case Phrase:
                return ((Phrase) e).getSentence().getId();
            case Token:
                return ((Token) e).getSentence().getId();
        }
        return null;
    }

    private <T extends NlpBaseElement> String getParentName(T e) {
        switch (e.getType()) {
            case Document:
                return "";
            case Sentence:
                return "document";
            case Phrase:
            case Token:
                return "sentence";
        }
        return null;
    }

    private Relation getRelation(Element e, String... argumentIds) {
        Relation r = new Relation();
        NamedNodeMap attributes = e.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            r.setProperty(attributes.item(j).getNodeName(), attributes.item(j).getNodeValue());
        }
        r.setId(r.getProperty("id"));
        for (int i = 0; i < argumentIds.length; i++) {
            r.setArgumentId(i, r.getProperty(argumentIds[i]));
        }
        r.setParent(getNlpBaseElement(getAncestorElement(e, getRelationParentTagName()), relationParent));
        return r;
    }

    private String getRelationParentTagName() {
        switch (relationParent) {
            case Document:
                return getDocumentTagName();
            case Sentence:
                return getSentenceTagName();
            case Phrase:
                return getPhraseTagName();
            case Token:
                return getTokenTagName();
        }
        return "";
    }

    private <T extends NlpBaseElement> List<T> getElementList(String tagName, String parentId, NlpBaseElementTypes type, String... addPropertiesFromTag) {
        NodeList nodes = parentId == null ?
                getNodes(tagName) :
                getNodes(tagName, parentId);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                NlpBaseElement s = getNlpBaseElement(e, type);
                list.add((T) s);
            }
        }
        if (list.size() > 0) {
            for (String t : addPropertiesFromTag) {
                addPropertiesFromTag(t, list);
            }
        }
        return list;
    }

    private NlpBaseElement getNlpBaseElement(Element e, NlpBaseElementTypes type) {
        if (e == null)
            return null;
        NlpBaseElement element = null;
        String id = getStringAttribute(e, getIdTagName());
        if (identityMap.containsKey(id)) {
            return identityMap.get(id);
        }
        switch (type) {
            case Document:
                element = new Document();
                break;
            case Sentence:
                Sentence s = new Sentence();
                s.setDocument((Document) getNlpBaseElement(getAncestorElement(e, documentTagName), NlpBaseElementTypes.Document));
                element = s;
                break;
            case Phrase:
                Phrase p = new Phrase();
                p.setSentence((Sentence) getNlpBaseElement(getAncestorElement(e, sentenceTagName), NlpBaseElementTypes.Sentence));
                element = p;
                break;
            case Token:
                Token t = new Token();
                t.setPhrase((Phrase) getNlpBaseElement(getAncestorElement(e, phraseTagName), NlpBaseElementTypes.Phrase));
                t.setSentence((Sentence) getNlpBaseElement(getAncestorElement(e, sentenceTagName), NlpBaseElementTypes.Sentence));
                element = t;
                break;
        }
        element.setId(id);
        element.setStart(getIntAttribute(e, getStartTagName()));
        element.setEnd(getIntAttribute(e, getEndTagName()));
        element.setText(getStringAttribute(e, getTextTagName()));
        NamedNodeMap attributes = e.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            element.addPropertyValue(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        NodeList children = e.getChildNodes();
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element c = (Element) children.item(i);
                if (isTextOnlyNode(c))
                    element.addPropertyValue(c.getTagName(), c.getTextContent());
            }
        }
        return element;
    }

    private boolean isTextOnlyNode(Element n) {
        if (!n.hasChildNodes())
            return true;
        if (n.getChildNodes().getLength() > 1)
            return false;
        return n.getFirstChild().getNodeType() == Node.TEXT_NODE;
    }

    private SpanBasedElement getElementContainer(Element e) {
        if (e == null)
            return null;
        SpanBasedElement element = new SpanBasedElement();
        element.setStart(getIntAttribute(e, getStartTagName()));
        element.setEnd(getIntAttribute(e, getEndTagName()));
        element.setText(getStringAttribute(e, getTextTagName()));
        return element;
    }


    private Element getAncestorElement(Element n, String ancestorTagName) {
        if (n == null || ancestorTagName == null || n.getTagName() == ancestorTagName)
            return n;
        Node parent = n.getParentNode();
        if (parent == null)
            return null;
        while (parent != null && parent.getNodeType() != Node.ELEMENT_NODE)
            parent = parent.getParentNode();
        return getAncestorElement((Element) parent, ancestorTagName);
    }

    private NodeList getNodes(String tagName, String parentId) {
        String query = parentId == null ?
                String.format("//%s", tagName) :
                String.format("//*[@id='%s']//%s", parentId, tagName);
        try {
            return (NodeList) xpath.evaluate(query, xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NodeList getNodes(String tagName) {
        return xmlDocument.getElementsByTagName(tagName);
    }

    private Node getNodeById(String id) {
        String query = String.format("//*[@id='%s']", id);
        try {
            return (Node) xpath.evaluate(query, xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Integer getIntAttribute(Element e, String name) {
        String a = getStringAttribute(e, name);
        if (a != null)
            return Integer.parseInt(e.getAttribute(name));
        return -1;
    }

    private String getStringAttribute(Element e, String name) {

        String a = getAttribute(e, name);
        if (a != null)
            return a;
        a = getAttribute(e, name.toUpperCase());
        if (a != null)
            return a;
        a = getAttribute(e, name.toUpperCase());
        if (a != null)
            return a;
        return null;
    }

    private String getAttribute(Element e, String name) {
        if (e.hasAttribute(name))
            return e.getAttribute(name);
        NodeList innerElement = e.getElementsByTagName(name);
        if (innerElement.getLength() > 0 && innerElement.item(0).getParentNode() == e)
            return innerElement.item(0).getTextContent();
        return null;
    }

    public NlpBaseElementTypes getRelationParent() {
        return relationParent;
    }

    public void setRelationParent(NlpBaseElementTypes relationParent) {
        this.relationParent = relationParent;
    }
}
