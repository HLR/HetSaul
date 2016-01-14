package edu.illinois.cs.cogcomp.saulexamples;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * The default properties used for all the examples
 *
 * @author Parisa Kordjamshidi
 * @author Christos Christodoulopoulos
 * @since 10/16/15
 */
public class ExamplesConfigurator extends Configurator {
    public static final Property TREEBANK_HOME = new Property("treebankHome", "../data/treebank");
    public static final Property PROPBANK_HOME = new Property("propbankHome","../data/propbank");
	public static final Property MODELS_DIR = new Property("modelsDir", "../models/");
    public static final Property USE_CURATOR = new Property("useCurator", Configurator.FALSE);

    // This is used to determine the parse view in SRL experiments (can be ViewNames.GOLD or ViewNames.STANFORD)
    // For replicating the published experiments this needs to be GOLD
    public static final Property SRL_PARSE_VIEW = new Property("srlParseView", ViewNames.PARSE_GOLD);

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TREEBANK_HOME, PROPBANK_HOME, MODELS_DIR, USE_CURATOR, SRL_PARSE_VIEW};
        return new ResourceManager(generateProperties(properties));
    }
}
