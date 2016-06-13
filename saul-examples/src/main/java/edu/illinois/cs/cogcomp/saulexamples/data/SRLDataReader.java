package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.PropbankReader;

import java.util.ArrayList;
import java.util.List;


/**
 * A reader that interfaces with {@link PropbankReader} and collects all the entities required by {@code SRLDataModel}
 *
 * @author Christos Christodoulopoulos
 */
public class SRLDataReader {
	private final PropbankReader reader;
	public List<TextAnnotation> textAnnotations;

	public SRLDataReader(String treebankHome, String propbankHome, String[] sections) throws Exception {
        textAnnotations = new ArrayList<>();
        reader = new PropbankReader(treebankHome, propbankHome, sections, ViewNames.SRL_VERB, true);
    }
    public SRLDataReader(String treebankHome, String propbankHome, int fromSection, int toSection) throws Exception {
        List<String> sections = new ArrayList<>();
        for (int i = fromSection; i <= toSection; i++) {
            sections.add(String.format("%02d", i));
        }
        textAnnotations = new ArrayList<>();
        reader = new PropbankReader(treebankHome, propbankHome, sections.toArray(new String[sections.size()]), ViewNames.SRL_VERB, true);
    }

	public void readData() {
		while (reader.hasNext()) {
			TextAnnotation ta = reader.next();
			if (!ta.hasView(ViewNames.SRL_VERB))
				continue;

			textAnnotations.add(ta);
		}
	}
}
