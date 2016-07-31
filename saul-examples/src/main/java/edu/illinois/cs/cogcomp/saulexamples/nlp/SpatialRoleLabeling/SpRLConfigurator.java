/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling;

import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * Created by taher on 7/28/16.
 */
public class SpRLConfigurator extends Configurator {

    public static final Property TEST_DIR = new Property("testDir","data/SpRL/2013/ConfluenceProject/gold");
    public static final Property TRAIN_DIR = new Property("trainDir","data/SpRL/2013/ConfluenceProject/train");
    public static final Property MODELS_DIR = new Property("modelsDir","models");

    public static final Property VERSION = new Property("version","2013");
    public static final Property IS_TRAINING = new Property("isTraining", Configurator.TRUE);

    /*********** SRL PROPERTIES ***********/
    // The (sub)directory to store and retrieve the trained SpRL models (to be used with MODELS_DIR)
    public static final Property SpRL_MODEL_DIR = new Property("sprlModelDir", "sprl");

    public static final Property SpRL_JAR_MODEL_PATH = new Property("jarModelPath","models");

    // A file to store the predictions of the SpRL classifier
    public static final Property SpRL_OUTPUT_FILE = new Property("sprlOutputFile", "sprl-predictions.txt");

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TEST_DIR, TRAIN_DIR, IS_TRAINING, VERSION, MODELS_DIR,
                SpRL_MODEL_DIR, SpRL_OUTPUT_FILE, SpRL_JAR_MODEL_PATH};
        return new ResourceManager(generateProperties(properties));
    }
}
