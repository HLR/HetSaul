/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.lbjava.parse.LineByLine;

import java.util.LinkedList;

/**
 * Represents a newsgroup post from the 20 newsgroups corpus
 * (http://people.csail.mit.edu/jrennie/20Newsgroups/).
 **/
public class Post
{
    /** The label of this post; ie., the newsgroup in which it was posted. */
    private String newsgroup;
    /** The contents of the "From" header. */
    private String fromHeader;
    /** The contents of the "Subject" header. */
    private String subjectHeader;
    /**
     * The entire contents of the newsgroup post except for headers, split up
     * into lines and words.
     **/
    private String[][] body;


    /**
     * Constructor.
     *
     * @param group    The newsgroup in which this post appeared.
     * @param from     The from header.
     * @param subject  The subject header.
     * @param body     The body of the post.
     **/
    public Post(String group, String from, String subject, String[][] body) {
        newsgroup = group;
        fromHeader = from;
        subjectHeader = subject;
        this.body = body;
    }


    /**
     * Constructor.
     *
     * @param file The path of a file containing a newsgroup post.  This string
     *             should contain at least one subdirectory, as the last
     *             subdirectory will be stored in the {@link #newsgroup} field
     *             as the label of this <code>Post</code> object.  The file
     *             named by this string must contain "From:" and "Subject:"
     *             header lines in its first two lines in either order followed
     *             by a blank line.  The rest of the file is considered the
     *             body of the post.
     **/
    public Post(String file) {
        assert file != null : "Null argument sent to Post constructor.";
        assert file.indexOf("/") != -1
                : "Filename sent to Post constructor must contain subdirectories";

        while (file.indexOf("//") != -1) {
            file = file.replaceAll("\\/\\/", "/");
        }
        if (file.endsWith("/")) file = file.substring(0, file.length() - 1);

        String[] path = file.split("\\/");
        newsgroup = path[path.length - 2];
        LineByLine parser =
                new LineByLine(file) {
                    public Object next() {
                        return readLine();
                    }
                };

        String line = (String) parser.next();
        assert line != null : "File sent to Post constructor contains no lines.";
        assert line.startsWith("From:") || line.startsWith("Subject:")
                : "File set to Post constructor is missing expected headers.";
        if (line.startsWith("From:")) fromHeader = line.substring(6);
        else subjectHeader = line.substring(9);
        line = (String) parser.next();
        assert line != null : "File sent to Post constructor contains no lines.";
        assert line.startsWith("From:") || line.startsWith("Subject:")
                : "File set to Post constructor is missing expected headers.";
        if (line.startsWith("From:")) fromHeader = line.substring(6);
        else subjectHeader = line.substring(9);
        assert fromHeader != null && subjectHeader != null
                : "File set to Post constructor is missing expected headers.";
        line = (String) parser.next();

        LinkedList<String[]> lines = new LinkedList<>();
        for (line = (String) parser.next(); line != null;
             line = (String) parser.next())
            lines.add(line.split(" +"));

        body = lines.toArray(new String[0][]);
    }


    /** Accesses the {@link #newsgroup} field. */
    public String getNewsgroup() { return newsgroup; }
    /** Accesses the {@link #fromHeader} field. */
    public String getFromHeader() { return fromHeader; }
    /** Accesses the {@link #subjectHeader} field. */
    public String getSubjectHeader() { return subjectHeader; }
    /** Returns the number of lines in the {@link #body}. */
    public int bodySize() { return body == null ? 0 : body.length; }

    /**
     * Returns the number of words in the specified line of {@link #body}.
     *
     * @param line The index of the line.
     **/
    public int lineSize(int line) {
        return body == null || line < 0 || line >= body.length
                || body[line] == null
                ? 0 : body[line].length;
    }

    /**
     * Returns the specified word in the {@link #body} of the post.
     *
     * @param line The index of the line containing the word.
     * @param word The index of the word in the specified line.
     * @return The word from the body at the specified indexes.
     **/
    public String getBodyWord(int line, int word) {
        return body == null || line < 0 || line >= body.length
                || body[line] == null || word < 0 || word >= body[line].length
                ? null : body[line][word];
    }
}

