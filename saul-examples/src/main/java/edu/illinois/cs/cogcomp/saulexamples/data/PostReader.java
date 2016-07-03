/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.lbjava.parse.LineByLine;

import java.util.ArrayList;

/**
 * This parser takes a list of file names containing newsgroup posts as input
 * and returns {@link Post} objects representing those posts.  The list of
 * file names is provided to this parser in a file containing one name per
 * line.  Each name should include at least one subdirectory, since the
 * subdirectory containing the newsgroup post is taken as its label.  For
 * example, the contents of the input file might look like this:
 *
 * <p>
 * <pre>
 * data/20news/alt.atheism/49960
 * data/20news/comp.graphics/51060
 * data/20news/talk.politics.misc/51119
 * data/20news/talk.religion.misc/51120
 * </pre>
 **/

public class PostReader extends LineByLine
{
    /**
     * Constructor.
     *
     * @param file The name of file containing names of files that contain one
     *             news group post each.
     **/
    public ArrayList<Post> docs= new ArrayList<>();
    public PostReader(String file) {
        super(file);
        String files;
        while(file!=null)
        {   files=readLine();
           if(files==null) break;
            docs.add(new Post(files));
        }


    }



    /**
     * Returns the next {@link Post} object representing a newsgroup post from
     * the named file, or <code>null</code> if no more remain.
     **/


    public Object next() {

        String file = readLine();

        if (file == null){
            System.out.println("EMPTY\n");
            return null;}
        return new Post(file);
    }
}

