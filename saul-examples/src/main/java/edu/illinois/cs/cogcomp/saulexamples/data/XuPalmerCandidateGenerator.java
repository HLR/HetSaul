package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseUtils;
import edu.illinois.cs.cogcomp.srl.core.ArgumentCandidateGenerator;
import edu.illinois.cs.cogcomp.srl.core.SRLManager;
import edu.illinois.cs.cogcomp.srl.verb.XuePalmerCandidateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Parisa on 12/18/15.
 */
public class XuPalmerCandidateGenerator extends  ArgumentCandidateGenerator{

     private static final Logger log = LoggerFactory.getLogger(XuePalmerCandidateGenerator.class);

        public XuPalmerCandidateGenerator(SRLManager manager) {
            super(manager);
        }

        public String getCandidateViewName() {
            return "XuePalmerHeuristicView";
        }

        public List<Constituent> generateSaulCandidates(Constituent predicate, Tree<String> tree){{
            Constituent predicateClone = predicate.cloneForNewView(this.getCandidateViewName());
            TextAnnotation ta = predicateClone.getTextAnnotation();
            int sentenceId = ta.getSentenceId(predicateClone);
          //  Tree tree = ParseUtils.getParseTree(this.manager.defaultParser, ta, sentenceId);
            Tree spanLabeledTree = ParseUtils.getSpanLabeledTree(tree);
            int sentenceStart = ta.getSentence(sentenceId).getStartSpan();
            int predicatePosition = predicateClone.getStartSpan() - sentenceStart;
            HashSet out = new HashSet();
            List yield = spanLabeledTree.getYield();
            if(predicatePosition >= yield.size()) {
                System.out.println(ta);
                System.out.println("Predicate: " + predicatePosition + "\t" + predicateClone);
                System.out.println(tree);
                System.out.println(spanLabeledTree);
                System.out.println("Tree view");
          //      System.out.println(ta.getView(this.manager.defaultParser));
                throw new RuntimeException();
            } else {
                Tree predicateTree = (Tree)yield.get(predicatePosition);
                Tree currentNode = predicateTree.getParent();
                boolean done = false;

                ArrayList output;
                Iterator ex;
                String caller;
                while(!done) {
                    if(currentNode.isRoot()) {
                        done = true;
                    } else {
                        output = new ArrayList();
                        ex = currentNode.getParent().getChildren().iterator();

                        while(ex.hasNext()) {
                            Tree callerClass = (Tree)ex.next();
                            Pair callerMethod = (Pair)callerClass.getLabel();
                            IntPair lineNumber = (IntPair)callerMethod.getSecond();
                            if(!lineNumber.equals(predicateClone.getSpan()) && (predicatePosition < lineNumber.getFirst() || predicateClone.getEndSpan() > lineNumber.getSecond())) {
                                caller = (String)callerMethod.getFirst();
                                int start = lineNumber.getFirst() + sentenceStart;
                                int end = lineNumber.getSecond() + sentenceStart;
                                output.add(this.getNewConstituent(ta, predicateClone, start, end));
                                if(caller.startsWith("PP")) {
                                    Iterator i$ = callerClass.getChildren().iterator();

                                    while(i$.hasNext()) {
                                        Tree child = (Tree)i$.next();
                                        int candidateStart = ((IntPair)((Pair)child.getLabel()).getSecond()).getFirst() + sentenceStart;
                                        int candidateEnd = ((IntPair)((Pair)child.getLabel()).getSecond()).getSecond() + sentenceStart;
                                        output.add(this.getNewConstituent(ta, predicateClone, candidateStart, candidateEnd));
                                    }
                                }
                            }
                        }

                        out.addAll(output);
                        currentNode = currentNode.getParent();
                    }
                }

                output = new ArrayList();
                ex = out.iterator();

                while(ex.hasNext()) {
                    Constituent callerClass2 = (Constituent)ex.next();
                    if(!ParseTreeProperties.isPunctuationToken(callerClass2.getSurfaceForm())) {
                        output.add(callerClass2);
                    }
                }

                if(log.isDebugEnabled()) {
                    Exception ex1 = new Exception();
                    String callerClass1 = ex1.getStackTrace()[1].getClassName();
                    String callerMethod1 = ex1.getStackTrace()[1].getMethodName();
                    int lineNumber1 = ex1.getStackTrace()[1].getLineNumber();
                    caller = callerClass1 + "." + callerMethod1 + ":" + lineNumber1;
                    log.debug("Candidates for {} from heuristic: {}. Call from {}", new String[]{predicateClone.toString(), output.toString(), caller});
                }

                return output;
            }
        }

        }

        public List<Constituent> generateCandidates(Constituent predicate) {
            Constituent predicateClone = predicate.cloneForNewView(this.getCandidateViewName());
            TextAnnotation ta = predicateClone.getTextAnnotation();
            int sentenceId = ta.getSentenceId(predicateClone);
            Tree tree = ParseUtils.getParseTree(this.manager.defaultParser, ta, sentenceId);
            Tree spanLabeledTree = ParseUtils.getSpanLabeledTree(tree);
            int sentenceStart = ta.getSentence(sentenceId).getStartSpan();
            int predicatePosition = predicateClone.getStartSpan() - sentenceStart;
            HashSet out = new HashSet();
            List yield = spanLabeledTree.getYield();
            if(predicatePosition >= yield.size()) {
                System.out.println(ta);
                System.out.println("Predicate: " + predicatePosition + "\t" + predicateClone);
                System.out.println(tree);
                System.out.println(spanLabeledTree);
                System.out.println("Tree view");
                System.out.println(ta.getView(this.manager.defaultParser));
                throw new RuntimeException();
            } else {
                Tree predicateTree = (Tree)yield.get(predicatePosition);
                Tree currentNode = predicateTree.getParent();
                boolean done = false;

                ArrayList output;
                Iterator ex;
                String caller;
                while(!done) {
                    if(currentNode.isRoot()) {
                        done = true;
                    } else {
                        output = new ArrayList();
                        ex = currentNode.getParent().getChildren().iterator();

                        while(ex.hasNext()) {
                            Tree callerClass = (Tree)ex.next();
                            Pair callerMethod = (Pair)callerClass.getLabel();
                            IntPair lineNumber = (IntPair)callerMethod.getSecond();
                            if(!lineNumber.equals(predicateClone.getSpan()) && (predicatePosition < lineNumber.getFirst() || predicateClone.getEndSpan() > lineNumber.getSecond())) {
                                caller = (String)callerMethod.getFirst();
                                int start = lineNumber.getFirst() + sentenceStart;
                                int end = lineNumber.getSecond() + sentenceStart;
                                output.add(this.getNewConstituent(ta, predicateClone, start, end));
                                if(caller.startsWith("PP")) {
                                    Iterator i$ = callerClass.getChildren().iterator();

                                    while(i$.hasNext()) {
                                        Tree child = (Tree)i$.next();
                                        int candidateStart = ((IntPair)((Pair)child.getLabel()).getSecond()).getFirst() + sentenceStart;
                                        int candidateEnd = ((IntPair)((Pair)child.getLabel()).getSecond()).getSecond() + sentenceStart;
                                        output.add(this.getNewConstituent(ta, predicateClone, candidateStart, candidateEnd));
                                    }
                                }
                            }
                        }

                        out.addAll(output);
                        currentNode = currentNode.getParent();
                    }
                }

                output = new ArrayList();
                ex = out.iterator();

                while(ex.hasNext()) {
                    Constituent callerClass2 = (Constituent)ex.next();
                    if(!ParseTreeProperties.isPunctuationToken(callerClass2.getSurfaceForm())) {
                        output.add(callerClass2);
                    }
                }

                if(log.isDebugEnabled()) {
                    Exception ex1 = new Exception();
                    String callerClass1 = ex1.getStackTrace()[1].getClassName();
                    String callerMethod1 = ex1.getStackTrace()[1].getMethodName();
                    int lineNumber1 = ex1.getStackTrace()[1].getLineNumber();
                    caller = callerClass1 + "." + callerMethod1 + ":" + lineNumber1;
                    log.debug("Candidates for {} from heuristic: {}. Call from {}", new String[]{predicateClone.toString(), output.toString(), caller});
                }

                return output;
            }
        }
    }

