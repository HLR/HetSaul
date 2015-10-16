package edu.illinois.cs.cogcomp.saulexamples;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.ResourceManager;

import java.util.Properties;

/**
 * Created by Parisa on 10/16/15.
 */
public class ExamplesConfigurator extends Configurator {
    public static final Pair<String, String> TREEBANK_HOME = new Pair<>("treebankHome", "data/treebank");
    public static final Pair<String,String> PROPBANK_HOME = new Pair<> ("propbankHome","data/propbank_1/data");
    @Override
    public ResourceManager getDefaultConfig() {
        Properties properties = new Properties();
        properties.setProperty(TREEBANK_HOME.getFirst(), TREEBANK_HOME.getSecond());
        properties.setProperty(PROPBANK_HOME.getFirst(), PROPBANK_HOME.getSecond());
        return new ResourceManager(properties);
    }
}
