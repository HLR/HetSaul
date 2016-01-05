package edu.illinois.cs.cogcomp.saulexamples;

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
    public static final Property TREEBANK_HOME = new Property("treebankHome", "data/treebank");
    public static final Property PROPBANK_HOME = new Property("propbankHome","data/propbank");
	public static final Property MODELS_DIR = new Property("modelsDir", "models/");

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TREEBANK_HOME, PROPBANK_HOME, MODELS_DIR};
        return new ResourceManager(generateProperties(properties));
    }
}
