package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Reads documents, given a directory
 * 
 * @author Vivek Srikumar
 * 
 */
public class DocumentReader implements Parser {
    
    private final List<File> files;
    
    private int currentFileId;
    public ArrayList<Document> docs= new ArrayList<>();

    public DocumentReader(String directory) throws IOException {
        File d = new File(directory);
        
        if (!d.exists()) {
            System.err.println(directory + " does not exist!");
            System.exit(-1);
        }
        
        if (!d.isDirectory()) {
            System.err.println(directory + " is not a directory!");
            System.exit(-1);
        }

        files = new ArrayList<>();
        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                files.addAll(Arrays.asList(f.listFiles()));
        }
        
        Collections.shuffle(files);
        currentFileId = 0;
    }
      for (File f: files){
          String splitter = File.separator.replace("\\","\\\\");
          String[] split = f.getPath().split(splitter);

        String label = split[split.length - 2];

        docs.add(new Document(f, label));}
    }
    
    public void close() {
    }
    
    public Object next() {
        
        if (currentFileId < files.size()) {
            File file = files.get(currentFileId++);
            
            String[] split = file.getPath().split(File.separator);
            
            String label = split[split.length - 2];
            
            try {
                return new Document(file, label);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
                return null;
            }
        } else
            return null;
        
    }
    
    public void reset() {
        currentFileId = 0;
    }
}
