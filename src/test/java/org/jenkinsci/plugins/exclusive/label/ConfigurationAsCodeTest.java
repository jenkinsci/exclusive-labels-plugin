package org.jenkinsci.plugins.exclusive.label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import io.jenkins.plugins.casc.misc.junit.jupiter.WithJenkinsConfiguredWithCode;
import org.junit.jupiter.api.Test;

@WithJenkinsConfiguredWithCode
class ConfigurationAsCodeTest {

    @Test
    @ConfiguredWithCode("configuration-as-code.yml")
    void should_support_configuration_as_code(JenkinsConfiguredWithCodeRule r) {
        ExclusiveLabels exclusiveLabels =
                r.jenkins.getExtensionList(ExclusiveLabels.class).get(0);
        assertEquals("exclusive1 exclusive2", exclusiveLabels.getLabelsInString());
        System.out.println("Exclusive labels in string: " + exclusiveLabels.getLabelsInString());
        System.out.println("Exclusive labels: " + exclusiveLabels.getExclusiveLabels());
        assertEquals(2, exclusiveLabels.getExclusiveLabels().size());
        assertTrue(exclusiveLabels.getExclusiveLabels().stream()
                .anyMatch(l -> l.getName().equals("exclusive1")));
        assertTrue(exclusiveLabels.getExclusiveLabels().stream()
                .anyMatch(l -> l.getName().equals("exclusive2")));
    }
}
