/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.NER;
        import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
        import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
        import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder;
        import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
        import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
        import edu.illinois.cs.cogcomp.core.io.IOUtils;
        import edu.illinois.cs.cogcomp.core.io.LineIO;
        import edu.illinois.cs.cogcomp.core.io.caches.DBHelper;
        import edu.illinois.cs.cogcomp.core.io.caches.TextAnnotationDBHandler;
        import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
        import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
        import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
        import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

public class NERDataReader implements Parser {
    static String ROOT_DIR = System.getProperty("user.dir");
    static String[] viewsToAdd = {ViewNames.LEMMA, ViewNames.POS, ViewNames.SHALLOW_PARSE};

    protected static Logger logger = LoggerFactory.getLogger(NERDataReader.class);

    public static final String CANDIDATE = "candidate";

    protected IResetableIterator<TextAnnotation> dataset;
    protected List<Constituent> candidates;
    private int currentCandidate, currentTextAnnotation;
    protected String viewName;
    protected final String file;

    private AnnotatorService preprocessor;

    public NERDataReader(String folder, String corpusName, String viewName) {
        this.file = folder;
        this.viewName = viewName;
        this.candidates = new ArrayList<>();
        String cacheDB = ROOT_DIR + File.separator + "data-cached" +
                File.separator + viewName + "-cache.db";
        TextAnnotationDBHandler dbHandler = new TextAnnotationDBHandler(cacheDB, new String[]{corpusName});

        if (!isCached(corpusName, cacheDB)) {
            dbHandler.initializeDatasets(cacheDB);
            List<TextAnnotation> textAnnotations = readData();
            int processed = 0;
            int total = textAnnotations.size();
            logger.info("Finished reading from {}.", this.file);
            for (TextAnnotation ta : textAnnotations) {
                TextAnnotation cachedTA = getTA(ta, cacheDB);
                if (cachedTA != null) ta = cachedTA;
                boolean viewsAdded = true;
                try {
                    for (String view : viewsToAdd)
                        viewsAdded &= getPreprocessor().addView(ta, view);
                } catch (AnnotatorException | RuntimeException e) {
                    logger.error("Unable to preprocess TextAnnotation {}. Skipping", ta.getId());
                    continue;
                }
                if (cachedTA == null)
                    dbHandler.addTextAnnotation(corpusName, ta);
                else if (viewsAdded)
                    dbHandler.updateTextAnnotation(ta);
                processed++;
                if (processed % 1000 == 0)
                    logger.info("Processed {} of {} TextAnnotations", processed, total);
            }
            logger.info("Finished pre-processing {} TextAnnotations.", processed);
        }
        dataset = dbHandler.getDataset(corpusName);
    }

    private boolean isCached(String corpusName, String cacheDB) {
        boolean isCached = false;
        Connection connection = DBHelper.getConnection(cacheDB);
        try {
            PreparedStatement stmt = connection.prepareStatement("select id from datasets where name = ?");
            stmt.setString(1, corpusName);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return false;
            int datasetId = rs.getInt("id");

            stmt = connection.prepareStatement("select * from sentencesToDataset where datasetId = ?");
            stmt.setInt(1, datasetId);
            rs = stmt.executeQuery();
            if (rs.next()) isCached = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isCached;
    }

    public List<TextAnnotation> readData() {
        List<TextAnnotation> textAnnotations = new ArrayList<>();
        String dataFolder = file;
        String[] files;
        try {
            files = IOUtils.lsFiles(dataFolder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read the contents of " + dataFolder);
        }
        for (String dataFile : files) {
            List<String> lines;
            try {
                lines = LineIO.read(dataFile);
                lines = lines.subList(2, lines.size()-1);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Couldn't read " + dataFile);
            }
            String corpusId = IOUtils.getFileName(dataFile);
            List<String> labels = new ArrayList<>();
            List<String> tokens = new ArrayList<>();
            int taId = 0;
            for (String line : lines) {
                if (line.isEmpty()) {
                    List<String[]> tokenizedSentence = Collections.singletonList(tokens.toArray(new String[tokens.size()]));
                    TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(
                            corpusId, String.valueOf(taId), tokenizedSentence);

                    if (isAllPunct(tokens)) {
                        logger.info("Skipping empty sentence {} ("+corpusId+":sent-{}).", ta.getText().trim(), ta.getId());
                        continue;
                    }
                    addView(ta, labels);
                    textAnnotations.add(ta);
                    labels.clear();
                    tokens.clear();
                    taId++;
                }
                else {
                    labels.add(line.split("\\s+")[0]);
                    tokens.add(line.split("\\s+")[5]);
                }
            }
        }
        return textAnnotations;
    }

    private TextAnnotation getTA(TextAnnotation ta, String dbFile) {
        int id = ta.getTokenizedText().hashCode();
        Connection connection = DBHelper.getConnection(dbFile);

        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement("select sentences.ta from sentences where id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return null;
            byte[] bytes = rs.getBytes(1);
            return SerializationHelper.deserializeTextAnnotationFromBytes(bytes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AnnotatorService getPreprocessor() {
        if (preprocessor == null) {
            preprocessor = null;
        }
        return preprocessor;
    }

    private boolean isAllPunct(List<String> tokens) {
        boolean allPunct = true;
        for (String token : tokens){
            allPunct &= token.matches("\\p{Punct}");
        }
        return allPunct;
    }

    private void addView(TextAnnotation ta, List<String> labels) {
        TokenLabelView labelView = new TokenLabelView(viewName, ta);
        List constituents = ta.getView(ViewNames.TOKENS).getConstituents();

        assert constituents.size() == labels.size();

        for (int i = 0; i < constituents.size(); ++i) {
            Constituent constituent = (Constituent) constituents.get(i);
            labelView.addTokenLabel(constituent.getStartSpan(), labels.get(i), 1.0D);
        }
        ta.addView(viewName, labelView);
    }

    public List<Constituent> candidateGenerator(TextAnnotation ta) {
        return getFinalCandidates(ta.getView(viewName), ta.getView(ViewNames.TOKENS).getConstituents());
    }

    protected List<Constituent> getFinalCandidates(View goldView, List<Constituent> candidates) {
        List<Constituent> finalCandidates = new ArrayList<>();
        for (Constituent c : candidates) {
            Constituent goldConst = getExactMatch(goldView, c);
            if (goldConst != null)
                finalCandidates.add(goldConst);
            else
                finalCandidates.add(new Constituent(CANDIDATE, viewName, c.getTextAnnotation(), c.getStartSpan(), c.getEndSpan()));
        }
        for (Constituent c : goldView.getConstituents()) {
            if (!finalCandidates.contains(c))
                finalCandidates.add(c);
        }
        return finalCandidates;
    }

    private Constituent getExactMatch(View view, Constituent c) {
        for (Constituent viewConst : view.getConstituents()) {
            if (viewConst.getSpan().equals(c.getSpan())) return viewConst;
        }
        return null;
    }

    /**
     * Fetches the next available data instance for training/testing. Also, pre-processes each new
     * {@link TextAnnotation} object before accessing its members.
     *
     * @return A {@link Constituent} (which might be a part of a {@link Relation},
     *         depending on the type of {@link View} )
     */
    @Override
    public Object next() {
        if (candidates.isEmpty() || candidates.size() == currentCandidate) {
            currentTextAnnotation++;
            if (!dataset.hasNext()) return null;
            TextAnnotation ta = dataset.next();
            if (!ta.hasView(viewName)) return next();
            candidates = candidateGenerator(ta);
            if (candidates.isEmpty()) return next();
            currentCandidate = 0;
            if (currentTextAnnotation % 1000 == 0)
                logger.info("Read {} TextAnnotations", currentTextAnnotation);
        }
        return candidates.get(currentCandidate++);
    }

    @Override
    public void reset() {
        currentCandidate = 0;
        candidates = new ArrayList<>();
        currentTextAnnotation = 0;
        dataset.reset();
    }

    @Override
    public void close() {

    }
}
