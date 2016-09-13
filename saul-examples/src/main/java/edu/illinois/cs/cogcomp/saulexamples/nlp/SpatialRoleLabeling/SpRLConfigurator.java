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

//    public static final Property TEST_DIR = new Property("testDir","data/SpRL/2013/ConfluenceProject/gold");
//    public static final Property TRAIN_DIR = new Property("trainDir","data/SpRL/2013/ConfluenceProject/train");
    public static final Property TEST_DIR = new Property("testDir","data/SpRL/2013/IAPR TC-12/gold");
    public static final Property TRAIN_DIR = new Property("trainDir","data/SpRL/2013/IAPR TC-12/train");
    public static final Property MODELS_DIR = new Property("modelsDir","models");

    public static final Property VERSION = new Property("version","2012");
    public static final Property IS_TRAINING = new Property("isTraining", Configurator.FALSE);

    /*********** SpRL PROPERTIES ***********/
    // The (sub)directory to store and retrieve the trained SpRL models (to be used with MODELS_DIR)
    public static final Property SpRL_MODEL_DIR = new Property("sprlModelDir", "sprl");

    // can take (Relation)
    public static final Property MODEL_NAME = new Property("modelName", "Relation");

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TEST_DIR, TRAIN_DIR, IS_TRAINING, VERSION, MODELS_DIR,
                SpRL_MODEL_DIR, MODEL_NAME};
        return new ResourceManager(generateProperties(properties));
    }
}
