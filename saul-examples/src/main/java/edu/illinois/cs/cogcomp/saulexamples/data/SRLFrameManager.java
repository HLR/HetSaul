package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

/**
 * A collection of function related to legal frames and sense ported from
 * {@code edu.illinois.cs.cogcomp.srl.core.SRLManager}, {@code edu.illinois.cs.cogcomp.srl.data.FramesManager}, etc.
 *
 * @author Christos Christodoulopoulos
 */
public class SRLFrameManager {
    private final static Logger log = LoggerFactory.getLogger(SRLFrameManager.class);

    /**
     * Indicates the 'null' label
     */
    public final static String NULL_LABEL = "<null>";

    public static final String UNKNOWN_VERB_CLASS = "UNKNOWN";

    private static final String[] allArguments = { NULL_LABEL, "A0", "A1",
            "A2", "A3", "A4", "A5", "AA", "AM-ADV", "AM-CAU", "AM-DIR",
            "AM-DIS", "AM-EXT", "AM-LOC", "AM-MNR", "AM-MOD", "AM-NEG",
            "AM-PNC", "AM-PRD", "AM-REC", "AM-TMP", "C-A0", "C-A1", "C-A2",
            "C-A3", "C-AM-ADV", "C-AM-CAU", "C-AM-DIS", "C-AM-EXT", "C-AM-LOC",
            "C-AM-MNR", "R-A0", "R-A1", "R-A2", "R-A3", "R-AA", "R-AM-ADV",
            "R-AM-LOC", "R-AM-MNR", "R-AM-PNC", "R-AM-TMP", "C-V", "C-A4",
            "C-AM-DIR", "C-AM-NEG", "C-AM-PNC", "C-AM-TMP", "R-A4", "R-AM-CAU",
            "R-AM-EXT" };

    private static final Set<String> allArgumentsSet = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(allArguments)));

    private static final String[] modifierArguments = { "AM-ADV", "AM-CAU",
            "AM-DIR", "AM-DIS", "AM-EXT", "AM-LOC", "AM-MNR", "AM-MOD",
            "AM-NEG", "AM-PNC", "AM-PRD", "AM-REC", "AM-TMP" };

    public static final Set<String> modifierArgumentSet = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(modifierArguments)));

    // XXX: Ignoring the sense "XX".
    private static final String[] allSenses = { "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21" };

    private final HashMap<String, Integer> senseToId;

    public HashMap<String, FrameData> frameData;

    public SRLFrameManager(String propFramesDir) throws Exception {
        log.info("Loading frames from {}", propFramesDir);

        try {
            readPropbankFrameData(propFramesDir + "/frames");
        } catch (Exception e) {
            log.error("Unable to load frames from {}", propFramesDir);
            throw new RuntimeException(e);
        }

        HashMap<String, Integer> label2Id = new HashMap<>();
        for (int i = 0; i < allSenses.length; i++) label2Id.put(allSenses[i], i);
        senseToId = label2Id;
    }

    private void readPropbankFrameData(String dir) throws Exception {
        frameData = new HashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);

        for (String file : IOUtils.lsFiles(dir, (dir1, name) -> name.endsWith("xml"))) {
            String fileName = IOUtils.getFileName(file);

            // A hack to deal with percent-sign in nombank. There is another
            // file called perc-sign that will fill this void.
            if (fileName.contains("percent-sign.xml"))
                continue;

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            NodeList predicateElements = doc.getElementsByTagName("predicate");

            for (int i = 0; i < predicateElements.getLength(); i++) {
                String lemma = IOUtils.stripFileExtension(fileName);

                FrameData fData = new FrameData(lemma);
                frameData.put(lemma, fData);

                NodeList roleSets = doc.getElementsByTagName("roleset");
                addRoleSets(fileName, lemma, fData, roleSets);
            }
        }
    }

    private void addRoleSets(String file, String lemma, FrameData fData, NodeList roleSets) {
        for (int i = 0; i < roleSets.getLength(); i++) {
            Element roleSet = (Element) roleSets.item(i);

            String sense = roleSet.getAttribute("id");

            String senseName = roleSet.getAttribute("name");

            // WTF frame makers?
            if (sense.equals("lionise.01"))
                sense = "lionize.01";

            if (sense.equals("oneslashonezeroth.01"))
                sense = "1-slash-10th.01";

            assert sense.startsWith(IOUtils.stripFileExtension(file)) || sense.startsWith(lemma) : lemma + "\t" + sense;

            String verbClass;
            if (roleSet.hasAttribute("vncls")) {
                verbClass = roleSet.getAttribute("vncls");
                if (verbClass.equals("-") || verbClass.length() == 0)
                    verbClass = UNKNOWN_VERB_CLASS;
            } else
                verbClass = UNKNOWN_VERB_CLASS;

            sense = sense.replaceAll(lemma + ".", "");

            fData.addSense(sense, senseName, verbClass);

            NodeList roles = roleSet.getElementsByTagName("role");

            for (int j = 0; j < roles.getLength(); j++) {
                Element role = (Element) roles.item(j);

                String argLabel = "A" + role.getAttribute("n");
                fData.addArgument(sense, argLabel);

                if (role.hasAttribute("descr")) {
                    String descr = role.getAttribute("descr");
                    fData.addArgumentDescription(sense, argLabel, descr);
                }

                NodeList elementsByTagName = role
                        .getElementsByTagName("vnrole");

                for (int roleId = 0; roleId < elementsByTagName.getLength(); roleId++) {
                    String vntheta = ((Element) (elementsByTagName.item(roleId))).getAttribute("vntheta");
                    fData.addArgumentVNTheta(sense, argLabel, vntheta);
                }

            } // end of arguments
        }
    }

    private Set<String> getPredicates() {
        return frameData.keySet();
    }

    private FrameData getFrame(String lemma) {
        return frameData.get(lemma);
    }

    /**
     * Get the set of valid senses for this predicate using the frame files.
     * For unknown predicates, only the sense 01 is allowed.
     */
    public Set<String> getLegalSenses(String predicate) {
        if (getPredicates().contains(predicate)) {
            Set<String> senses = getFrame(predicate).getSenses();

            // keep only senses that the model knows about
            senses.retainAll(this.senseToId.keySet());

            if (senses.size() > 0)
                return senses;
            else {
                log.error("Unknown predicate {}. Allowing only sense 01", predicate);
            }
        }
        return new HashSet<>(Collections.singletonList("01"));
    }

    /**
     * Get the name of a given sense for a given predicate
     */
    public String getSenseName(String predicate, String senseId) {
        if (getPredicates().contains(predicate)) {
            return getFrame(predicate).getSenseName(senseId);
        }
        else return UNKNOWN_VERB_CLASS;
    }

    /**
     * Get valid arguments for each sense of a given lemma from the frame files.
     * For unknown predicates, only the sense 01 is allowed with all arguments
     */
    public Map<String, Set<String>> getLegalLabelsForSense(String lemma) {
        Map<String, Set<String>> map = new HashMap<>();

        if (getPredicates().contains(lemma)) {
            FrameData frame = getFrame(lemma);
            for (String sense : frame.getSenses()) {
                Set<String> argsForSense = new HashSet<>(frame.getArgsForSense(sense));
                argsForSense.add(NULL_LABEL);
                map.put(sense, argsForSense);
            }
        }
        else map.put("01", allArgumentsSet);

        return map;
    }

    /**
     * Returns the set of legal arguments for all the sense of a given lemma.
     * This function uses the frame files to get the list of valid core arguments.
     * All the modifiers are treated as legal arguments. In addition, all C-args and
     * R-args of legal core/modifier arguments are also considered legal. For
     * unknown predicates, all arguments are legal.
     */
    public Set<String> getLegalArguments(String lemma) {
        Set<String> knownPredicates = getPredicates();
        if (knownPredicates.contains(lemma)) {
            HashSet<String> set = new HashSet<>(getFrame(lemma).getLegalArguments());

            set.addAll(modifierArgumentSet);

            for (String s : new ArrayList<>(set)) {
                set.add("C-" + s);
                set.add("R-" + s);
            }

            set.add(NULL_LABEL);

            return set;
        }
        else return allArgumentsSet;
    }

    public Set<String> getAllArguments() {
        return allArgumentsSet;
    }

    private class FrameData {
        private String lemma;

        private class SenseFrameData {
            Map<String, ArgumentData> argDescription = new HashMap<>();
            String verbClass = UNKNOWN_VERB_CLASS;
            String senseName;
        }

        private class ArgumentData {
            String description;
            Set<String> vnTheta = new HashSet<>();
        }

        private Map<String, SenseFrameData> senseFrameData;

        public FrameData(String lemma) {
            this.lemma = lemma;
            senseFrameData = new HashMap<>();
        }

        public void addSense(String sense, String senseName, String verbClass) {
            this.senseFrameData.put(sense, new SenseFrameData());
            this.senseFrameData.get(sense).verbClass = verbClass;
            this.senseFrameData.get(sense).senseName = senseName;
        }

        public Set<String> getSenses() {
            return this.senseFrameData.keySet();
        }

        public void addArgument(String sense, String arg) {
            assert this.senseFrameData.containsKey(sense);
            senseFrameData.get(sense).argDescription.put(arg, new ArgumentData());
        }

        public Set<String> getArgsForSense(String sense) {
            assert this.senseFrameData.containsKey(sense) : sense + " missing for predicate lemma " + this.lemma;
            return this.senseFrameData.get(sense).argDescription.keySet();
        }

        public void addArgumentDescription(String sense, String arg, String description) {
            assert this.senseFrameData.containsKey(sense);
            assert this.senseFrameData.get(sense).argDescription.containsKey(arg);
            senseFrameData.get(sense).argDescription.get(arg).description = description;
        }

        public void addArgumentVNTheta(String sense, String arg, String vnTheta) {
            assert this.senseFrameData.containsKey(sense);
            assert this.senseFrameData.get(sense).argDescription.containsKey(arg);
            senseFrameData.get(sense).argDescription.get(arg).vnTheta.add(vnTheta);
        }

        public String getSenseName(String sense) {
            assert this.senseFrameData.containsKey(sense) : sense
                    + " missing for predicate lemma " + this.lemma;
            return this.senseFrameData.get(sense).senseName;
        }

        public Set<String> getLegalArguments() {
            Set<String> l = new HashSet<>();
            for (String s : this.getSenses())
                l.addAll(this.getArgsForSense(s));
            return l;
        }
    }
}
