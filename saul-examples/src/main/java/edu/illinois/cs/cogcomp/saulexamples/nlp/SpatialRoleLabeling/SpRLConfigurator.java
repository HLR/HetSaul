package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * Created by taher on 7/28/16.
 */
public class SpRLConfigurator extends Configurator {
    public static final Property TEST_DIR = new Property("testDir","test");
    public static final Property TRAIN_DIR = new Property("testDir","train");

    public static final Property VERSION = new Property("version","2015");

    public static final Property IS_TRAINING = new Property("isTraining", Configurator.TRUE);


    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TEST_DIR, TRAIN_DIR, IS_TRAINING, VERSION};
        return new ResourceManager(generateProperties(properties));
    }
}
