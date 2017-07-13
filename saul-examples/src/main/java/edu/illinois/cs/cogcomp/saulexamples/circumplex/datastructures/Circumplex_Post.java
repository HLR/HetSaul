package edu.illinois.cs.cogcomp.saulexamples.circumplex.datastructures;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Abiyaz on 7/6/2017.
 */
public class Circumplex_Post{

    private String text;
    private double valenceLabel,arousalLabel;

    /**
     * Data file format has 5 fields:
     0 - Facebook post
     1 - Valence score #1
     2 - Valence score #2
     3 - Arousal score #1
     4 - Arousal score #2
     * @param csvFields The comma-separated line containing the 6 fields
     */
    public Circumplex_Post(String[] csvFields) {
        if (csvFields.length < 5) {
            System.err.println("Data doesn't conform to the formatting standard:\n" + Arrays.toString(csvFields));
            System.exit(-1);
        }
        text = stripQuotes(csvFields[0]);
        valenceLabel = ((Double.parseDouble(stripQuotes(csvFields[1])))+(Double.parseDouble(stripQuotes(csvFields[2]))))/2.0d;
        arousalLabel = ((Double.parseDouble(stripQuotes(csvFields[3])))+(Double.parseDouble(stripQuotes(csvFields[4]))))/2.0d;
    }

    /**
     * Constructor for testing. Only text is required (the label is inferred).
     * @param text The text of the tweet
     */
    public Circumplex_Post(String text) {
        this.text = text;
        valenceLabel = 0;
        arousalLabel = 0;
    }

    public List<String> getWords() {
        return Arrays.asList(text.split("\\s+"));
    }

    public double getValence() {
        return valenceLabel;
    }

    public double getArousal() {
        return arousalLabel;
    }

    private String stripQuotes(String str) {
        return str.replaceAll("\"", "");
    }
}
