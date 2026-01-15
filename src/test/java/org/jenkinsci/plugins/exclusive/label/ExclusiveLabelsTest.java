package org.jenkinsci.plugins.exclusive.label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ExclusiveLabelsTest {

    @Test
    void testConfigRoundtrip(JenkinsRule r) throws Exception {
        ExclusiveLabels exclusiveLabels =
                r.jenkins.getExtensionList(ExclusiveLabels.class).get(0);
        ((ExclusiveLabels.DescriptorImpl) exclusiveLabels.getDescriptor()).setLabelsInString("exclusive1 exclusive2");
        r.configRoundtrip();
        ExclusiveLabels after =
                r.jenkins.getExtensionList(ExclusiveLabels.class).get(0);
        assertEquals("exclusive1 exclusive2", after.getLabelsInString());
        assertEquals(2, after.getExclusiveLabels().size());
        assertTrue(after.getExclusiveLabels().stream().anyMatch(l -> l.getName().equals("exclusive1")));
        assertTrue(after.getExclusiveLabels().stream().anyMatch(l -> l.getName().equals("exclusive2")));
    }
}
