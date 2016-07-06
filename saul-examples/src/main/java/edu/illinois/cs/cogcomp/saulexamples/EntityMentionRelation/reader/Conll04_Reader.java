/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawSentence;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawToken;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Conll04_Reader implements Parser {

    public Vector<ConllRawToken> instances;
    public Vector<ConllRawSentence> sentences;
    public Vector<ConllRelation> relations;
    public String type_;

    public String[] entityLabels, relLabels;
    private int currentInstanceId;
    private int currentTokenId;
    private int currentPairId;
    private int currentSentenceId;

    public Conll04_Reader(String filename, String ty) {
        instances = new Vector<>();
        relations = new Vector<>();
        sentences = new Vector<>();
        entityLabels = new String[0];
        relLabels = new String[0];
        type_ = ty;
        List<String> lines = null;
        try {
            lines = LineIO.read(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        String[] tokens;


        ConllRawToken c = new ConllRawToken();

        ConllRelation r;
        int currSentId = 0;
        boolean sentEnd = false;
        ConllRawSentence sent = new ConllRawSentence(currSentId);

        ArrayList<String> entityal = new ArrayList<>();
        ArrayList<String> relal = new ArrayList<>();

        boolean relationSeen = false;
        int sentindex = 0;
        while (sentindex < lines.size()) {
            line = lines.get(sentindex);
            sentindex++;

            if (line.isEmpty()) {
                sentEnd = true;
                continue;
            }

            tokens = line.split("\t|\n");
            int s = tokens.length;
            if (s == 3) {
                relationSeen = true;
                r = new ConllRelation();
                r.sentId = currSentId;
                r.wordId1 = Integer.parseInt(tokens[0]);
                r.wordId2 = Integer.parseInt(tokens[1]);
                r.relType = tokens[2];
                relations.add(r);
                sent.addRelations(r);
                if (!relal.contains(tokens[2])) {
                    relal.add(tokens[2]);
                }
            } else {
                if (sentEnd) {
                    {
                        sentences.add(sent);
                        currSentId++;
                    }
                    sent = new ConllRawSentence(currSentId);
                }

                c = new ConllRawToken();
                c.entType = tokens[1];
                c.sentId = currSentId;
                c.wordId = Integer.parseInt(tokens[2]);
                c.setPOS(tokens[4]);
                c.setPhrase(tokens[5]);
                sent.addTokens(c);
                if (!tokens[1].trim().equals("O")) {
                    instances.add(c);
                    sent.setCurrentTokenAsEntity();
                    if (!entityal.contains(tokens[1])) {
                        entityal.add(tokens[1]);
                    }
                }

                sentEnd = false;
                relationSeen = false;
            }
        }

        entityLabels = entityal.toArray(entityLabels);
        relLabels = relal.toArray(relLabels);

        for (int counter = 0; counter < relations.size(); counter++) {
            int sindex = relations.elementAt(counter).sentId;
            relations.elementAt(counter).s.sentTokens.addAll(0, sentences.elementAt(sindex).sentTokens);
            relations.elementAt(counter).e1 = sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId1);
            relations.elementAt(counter).e2 = sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId2);

        }
    }

    public void printData() {
        System.out.println("printing total " + sentences.size() + " sentences");
        for (int i = 0; i < sentences.size(); i++) {
            sentences.elementAt(i).printEntities();
            sentences.elementAt(i).printRelations();
        }
        System.out.println("printing total " + instances.size() + " instances");
        for (int i = 0; i < instances.size(); i++) {
            instances.elementAt(i).printInstance();
        }
        System.out.println("printing total " + relations.size() + " relations");
        for (int i = 0; i < relations.size(); i++) {
            relations.elementAt(i).printRelation();
            System.out.println("WORD1:" + relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId1).phrase);
            System.out.println("WORD2:" + relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId2).phrase);
        }
    }

    public void close() {
    }

    public Object next() {

        if (type_.equals("Token")) {
            if (currentTokenId < instances.size()) {
                ConllRawToken file = instances.get(currentTokenId++);
                return file;//Document(file, label);
            } else
                return null;
        }
        if (type_.equals("Pair")) {
            if (currentPairId < relations.size()) {
                ConllRelation file = relations.get(currentPairId++);
                file.e1.setRelation(file);
                file.e2.setRelation(file);
                return file;
            } else
                return null;
        }
        return null;

    }

    public void reset() {
        if (type_.equals("Pair"))
            currentPairId = 0;
        if (type_.equals("Token"))
            currentTokenId = 0;
    }

    public void setId(int i) {
        if (type_.equals("Pair"))
            currentPairId = i;
        if (type_.equals("Token"))
            currentTokenId = i;
    }

    public Object[] get(String x, ConllRelation r) {
        Object[] a = null;
        if (x.equalsIgnoreCase("ConllRawToken")) {
            a = getTokens(r);
        }
        return a;
    }

    public ConllRawToken[] getTokens(ConllRelation r) {
        ConllRawToken[] a = new ConllRawToken[2];
        a[0] = r.e1;
        a[1] = r.e2;
        return a;
    }

    public static ConllRawToken PersonCandidate(ConllRelation t) {
        return t.e1;
    }

    public static ConllRawToken OrgCandidate(ConllRelation t) {
        return t.e2;
    }

}