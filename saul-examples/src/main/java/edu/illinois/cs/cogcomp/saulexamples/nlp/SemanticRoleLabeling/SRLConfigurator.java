/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * The default properties used for all the examples
 *
 * @author Parisa Kordjamshidi
 * @author Christos Christodoulopoulos
 */
public class SRLConfigurator extends Configurator {

    public static final Property TREEBANK_HOME = new Property("treebankHome", "../saul-examples/src/test/resources/SRLToy/treebank");
    public static final Property PROPBANK_HOME = new Property("propbankHome","../saul-examples/src/test/resources/SRLToy/propbank");

    public static final Property TEST_SECTION = new Property("testSection","00");

	public static final Property MODELS_DIR = new Property("modelsDir", "../models");
    public static final Property USE_CURATOR = new Property("useCurator", Configurator.FALSE);

    // The running mode of the program. Can be "true" for only testing, or  "false" for training
    public static final Property RUN_MODE = new Property("runMode", Configurator.TRUE);

    // The training mode for the examples. Can be "pipeline", "joint", or "other"
    public static final Property TRAINING_MODE = new Property("trainingMode", "joint");

    /*********** SRL PROPERTIES ***********/
    // The (sub)directory to store and retrieve the trained SRL models (to be used with MODELS_DIR)
    public static final Property SRL_MODEL_DIR = new Property("srlModelDir", "srl");

    public static final Property SRL_JAR_MODEL_PATH = new Property("jarModelPath","models");

    // This is used to determine the parse view in SRL experiments (can be ViewNames.GOLD or ViewNames.STANFORD)
    // For replicating the published experiments this needs to be GOLD
    public static final Property SRL_PARSE_VIEW = new Property("srlParseView", ViewNames.PARSE_GOLD);

    // A file to store the predictions of the SRL classifier (for argument types only)
    public static final Property SRL_OUTPUT_FILE = new Property("srlOutputFile", "srl-predictions.txt");

    // Whether to use gold predicates (if FALSE, predicateClassifier will be used instead)
    public static final Property SRL_GOLD_PREDICATES = new Property("srlGoldPredicates", Configurator.TRUE);

    // Whether to use gold argument boundaries (if FALSE, argumentXuIdentifierGivenApredicate will be used instead)
    public static final Property SRL_GOLD_ARG_BOUNDARIES = new Property("srlGoldArgBoundaries", Configurator.TRUE);

    // Should we use the pipeline during testing
    public static final Property SRL_TEST_PIPELINE = new Property("srlTestPipeLine", Configurator.FALSE);

    // Should we use constraints during testing
    public static final Property SRL_TEST_CONSTRAINTS = new Property("srlTestConstraints", Configurator.FALSE);

    // Should we train a predicate classifier given predicate candidates
    public static final Property SRL_TRAIN_PREDICATES = new Property("srlTrainPredicates", Configurator.FALSE);

    // Should we train an argument identifier given the XuPalmer argument candidates
    public static final Property SRL_TRAIN_ARG_IDENTIFIERS = new Property("srlArgIdentifier", Configurator.FALSE);

    // Should we train an argument type classifier given the XuPalmer argument candidates
    public static final Property SRL_TRAIN_ARG_TYPE = new Property("srlArgIdentifier", Configurator.FALSE);

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] properties = {TREEBANK_HOME, PROPBANK_HOME, MODELS_DIR, USE_CURATOR, TRAINING_MODE,
                SRL_MODEL_DIR, SRL_PARSE_VIEW, SRL_OUTPUT_FILE, SRL_GOLD_PREDICATES, SRL_GOLD_ARG_BOUNDARIES,
                SRL_TEST_PIPELINE, SRL_TEST_CONSTRAINTS,SRL_JAR_MODEL_PATH, RUN_MODE, SRL_TRAIN_PREDICATES,
                SRL_TRAIN_ARG_IDENTIFIERS,SRL_TRAIN_ARG_TYPE,TEST_SECTION};
        return new ResourceManager(generateProperties(properties));
    }
}
