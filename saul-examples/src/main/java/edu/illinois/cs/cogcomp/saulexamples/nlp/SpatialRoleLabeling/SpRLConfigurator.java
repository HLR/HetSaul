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
