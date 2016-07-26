/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import  java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Created by taher on 7/25/16.
 */
public class SpRLDataReader<T extends SpRLXmlDocument> {

    private final Class<T> jaxbClass;
    public List<T> documents;
    public final String corpusPath;

    public SpRLDataReader(String corpusPath, Class<T>  jaxbClass) {
        this.corpusPath = corpusPath;
        documents = new ArrayList<>();
        this.jaxbClass = jaxbClass;
    }

    public void readData() throws ParserConfigurationException, IOException, SAXException, JAXBException {
        Collection<File> files = getAllFiles(new File(corpusPath));
        documents = new ArrayList<>();
        for(File f : files) {
            documents.add(read2015XmlFile(f));
        }
    }

    private T read2015XmlFile(File xmlFile) throws SAXException, IOException, ParserConfigurationException, JAXBException {
        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        Node root = xmlDocument.getDocumentElement();
        JAXBContext jc = JAXBContext.newInstance(jaxbClass);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        T document = (T)unmarshaller.unmarshal(root);
        return  document;
    }
    private static Collection<File> getAllFiles(File dir) {
        Set<File> files = new HashSet<>();
        for (File entry : dir.listFiles()) {
            if (entry.isFile()) files.add(entry);
            else files.addAll(getAllFiles(entry));
        }
        return files;
    }
}
