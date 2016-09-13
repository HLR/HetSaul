/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.utilities.XmlModel;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
        File dir = new File(corpusPath);
        if(!dir.exists())
            throw new IOException("Cannot find '" + dir.getAbsolutePath() + "' path.");
        Collection<File> files = getAllFiles(dir);
        documents = new ArrayList<>();
        for(File f : files) {
            T doc = XmlModel.load(jaxbClass,f);
            doc.setFilename(f.getName());
            documents.add(doc);
        }
    }

    private static Collection<File> getAllFiles(File dir) {
        Set<File> files = new HashSet<>();
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                if(file.getName().toLowerCase().endsWith(".xml"))
                    files.add(file);
            }
            else files.addAll(getAllFiles(file));
        }
        return files;
    }
}
