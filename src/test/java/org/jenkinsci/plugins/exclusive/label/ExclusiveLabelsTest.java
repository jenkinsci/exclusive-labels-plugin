package org.jenkinsci.plugins.exclusive.label;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hudson.model.Label;
import hudson.model.Node;
import hudson.model.Queue.BuildableItem;
import hudson.model.Queue.Task;
import hudson.model.labels.LabelAtom;
import hudson.model.queue.CauseOfBlockage;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ExclusiveLabelsTest {

    private ExclusiveLabels exclusiveLabels;
    private Node mockNode;
    private BuildableItem mockItem;
    private Task mockTask;
    private Label mockLabel;

    @BeforeEach
    void setUp(JenkinsRule r) {
        exclusiveLabels = r.jenkins.getExtensionList(ExclusiveLabels.class).get(0);
        mockNode = mock(Node.class);
        mockItem = mock(BuildableItem.class);
        mockTask = mock(Task.class);
        mockLabel = mock(Label.class);
        mockItem.task = mockTask;
    }

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

    @Test
    void testCanTake_NoAssignedLabel_NodeHasExclusiveLabel() {
        // Setup exclusive labels
        ((ExclusiveLabels.DescriptorImpl) exclusiveLabels.getDescriptor()).setLabelsInString("exclusive1");
        
        // Mock node with exclusive label
        Set<LabelAtom> nodeLabels = new HashSet<>();
        nodeLabels.add(new LabelAtom("exclusive1"));
        when(mockNode.getAssignedLabels()).thenReturn(nodeLabels);
        
        // Mock task with no assigned label
        when(mockTask.getAssignedLabel()).thenReturn(null);
        
        CauseOfBlockage result = exclusiveLabels.canTake(mockNode, mockItem);
        assertNotNull(result);
        assertInstanceOf(ExclusiveLabels.NotExclusiveLabel.class, result);
    }

    @Test
    void testCanTake_AssignedLabelDoesNotContainNode() {
        // Mock assigned label that doesn't contain the node
        when(mockTask.getAssignedLabel()).thenReturn(mockLabel);
        when(mockLabel.contains(mockNode)).thenReturn(false);
        
        CauseOfBlockage result = exclusiveLabels.canTake(mockNode, mockItem);
        assertNull(result);
    }

    @Test
    void testCanTake_NodeHasNoExclusiveLabels() {
        // Setup exclusive labels
        ((ExclusiveLabels.DescriptorImpl) exclusiveLabels.getDescriptor()).setLabelsInString("exclusive1");
        
        // Mock node with no exclusive labels
        Set<LabelAtom> nodeLabels = new HashSet<>();
        nodeLabels.add(new LabelAtom("regular"));
        when(mockNode.getAssignedLabels()).thenReturn(nodeLabels);
        
        when(mockTask.getAssignedLabel()).thenReturn(mockLabel);
        when(mockLabel.contains(mockNode)).thenReturn(true);
        
        CauseOfBlockage result = exclusiveLabels.canTake(mockNode, mockItem);
        assertNull(result);
    }

    @Test
    void testCanTake_AssignedLabelDoesNotContainExclusiveLabel() {
        // Setup exclusive labels
        ((ExclusiveLabels.DescriptorImpl) exclusiveLabels.getDescriptor()).setLabelsInString("exclusive1");
        
        // Mock node with exclusive label
        Set<LabelAtom> nodeLabels = new HashSet<>();
        nodeLabels.add(new LabelAtom("exclusive1"));
        when(mockNode.getAssignedLabels()).thenReturn(nodeLabels);
        
        // Mock assigned label that doesn't contain exclusive label
        when(mockTask.getAssignedLabel()).thenReturn(mockLabel);
        when(mockLabel.contains(mockNode)).thenReturn(true);
        when(mockLabel.getName()).thenReturn("regular");
        
        CauseOfBlockage result = exclusiveLabels.canTake(mockNode, mockItem);
        assertNotNull(result);
        assertInstanceOf(ExclusiveLabels.NotExclusiveLabel.class, result);
    }

    @Test
    void testNotExclusiveLabel_ShortDescription() {
        when(mockNode.getDisplayName()).thenReturn("TestNode");
        
        ExclusiveLabels.NotExclusiveLabel blockage = new ExclusiveLabels.NotExclusiveLabel(mockNode);
        assertEquals("Node TestNode has exclusive label(s)", blockage.getShortDescription());
    }

    @Test
    void testDescriptor_EmptyLabelsString() {
        ExclusiveLabels.DescriptorImpl descriptor = new ExclusiveLabels.DescriptorImpl();
        descriptor.setLabelsInString("");
        
        assertTrue(descriptor.getLabels().isEmpty());
    }

    @Test
    void testDescriptor_NullLabelsString() {
        ExclusiveLabels.DescriptorImpl descriptor = new ExclusiveLabels.DescriptorImpl();
        descriptor.setLabelsInString(null);
        
        assertTrue(descriptor.getLabels().isEmpty());
    }

    @Test
    void testDescriptor_MultipleLabels() {
        ExclusiveLabels.DescriptorImpl descriptor = new ExclusiveLabels.DescriptorImpl();
        descriptor.setLabelsInString("label1 label2 label3");
        
        assertEquals(3, descriptor.getLabels().size());
        assertTrue(descriptor.getLabels().stream().anyMatch(l -> l.getName().equals("label1")));
        assertTrue(descriptor.getLabels().stream().anyMatch(l -> l.getName().equals("label2")));
        assertTrue(descriptor.getLabels().stream().anyMatch(l -> l.getName().equals("label3")));
    }

    @Test
    void testDescriptor_DisplayName() {
        ExclusiveLabels.DescriptorImpl descriptor = new ExclusiveLabels.DescriptorImpl();
        assertEquals("Exclusive labels", descriptor.getDisplayName());
    }
}
