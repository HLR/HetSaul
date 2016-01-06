package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.saulexamples.ExamplesConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SRLFrameManagerTest {

    private SRLFrameManager frameManager;

    @Before
    public void setUp() throws Exception {
        ResourceManager rm = new ExamplesConfigurator().getDefaultConfig();
        frameManager = new SRLFrameManager(rm.getString(ExamplesConfigurator.PROPBANK_HOME.key));
    }

    @Test
    public void testGetLegalSenses() throws Exception {
        Set<String> legalSenses = frameManager.getLegalSenses("go");
        assertEquals(21, legalSenses.size());
        assertTrue(legalSenses.contains("12"));
        assertEquals("experience, undergo", frameManager.getSenseName("go", "12"));
    }

    @Test
    public void testGetLegalLabelsForSense() throws Exception {
        Map<String, Set<String>> labelsForSense = frameManager.getLegalLabelsForSense("give");
        assertEquals(9, labelsForSense.size());
        // give.07 should have A0, A1 and <null>
        Set<String> argSet07 = labelsForSense.get("07");
        assertEquals(3, argSet07.size());
        assertTrue(argSet07.contains("A1"));
    }

    @Test
    public void testGetLegalArguments() throws Exception {
        Set<String> arguments = frameManager.getLegalArguments("include");
        Set<String> illegalArguments = new HashSet<>(frameManager.getAllArguments());
        assertEquals(49, arguments.size());
        illegalArguments.removeAll(arguments);
        assertEquals(10, illegalArguments.size());
        // A3, A4 and A5 are illegal arguments for "include"
        assertTrue(illegalArguments.contains("A3"));
        assertTrue(illegalArguments.contains("A4"));
        assertTrue(illegalArguments.contains("A5"));
    }
}