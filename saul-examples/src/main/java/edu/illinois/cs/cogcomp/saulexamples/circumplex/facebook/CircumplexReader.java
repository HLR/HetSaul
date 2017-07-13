package edu.illinois.cs.cogcomp.saulexamples.circumplex.facebook;

import edu.illinois.cs.cogcomp.saulexamples.circumplex.datastructures.Circumplex_Post;
import edu.illinois.cs.cogcomp.core.io.LineIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abiyaz on 7/6/2017.
 */
public class CircumplexReader {
    public List<Circumplex_Post> posts;

    public CircumplexReader(String dataFile) {
        posts = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = LineIO.readGZip(dataFile);
        } catch (IOException e) {
            System.err.println("Unable to read " + dataFile + ". Exiting...");
            System.exit(-1);
        }
        for (String line : lines.subList(1,lines.size())) {
            if (!line.isEmpty()) {
                line = line.replaceAll(",","");
                String text = line.substring(0,line.length()-4);
                String valence1 = line.substring(line.length()-4,line.length()-3);
                String valence2 = line.substring(line.length()-3,line.length()-2);
                String arousal1 = line.substring(line.length()-2,line.length()-1);
                String arousal2 = line.substring(line.length()-1,line.length());
                posts.add(new Circumplex_Post(new String[]{text,valence1,valence2,arousal1,arousal2}));
            }
        }
    }
}
