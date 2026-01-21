package org.jenkinsci.plugins.exclusive.label;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.labels.LabelAtom;
import hudson.model.labels.LabelExpression;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExclusiveLabelVisitorTest {

    private ExclusiveLabelVisitor visitor;
    private Set<LabelAtom> exclusiveLabels;
    private LabelAtom label1;
    private LabelAtom label2;
    private LabelAtom label3;

    @BeforeEach
    void setUp() {
        visitor = new ExclusiveLabelVisitor();
        exclusiveLabels = new HashSet<>();
        label1 = new LabelAtom("exclusive1");
        label2 = new LabelAtom("exclusive2");
        label3 = new LabelAtom("regular");
        
        exclusiveLabels.add(label1);
        exclusiveLabels.add(label2);
    }

    @Test
    void testOnAtom_ExclusiveLabel() {
        assertTrue(visitor.onAtom(label1, exclusiveLabels));
        assertTrue(visitor.onAtom(label2, exclusiveLabels));
    }

    @Test
    void testOnAtom_NonExclusiveLabel() {
        assertFalse(visitor.onAtom(label3, exclusiveLabels));
    }

    @Test
    void testOnParen() {
        LabelExpression.Paren paren = new LabelExpression.Paren(label1);
        assertTrue(visitor.onParen(paren, exclusiveLabels));
        
        LabelExpression.Paren parenNonExclusive = new LabelExpression.Paren(label3);
        assertFalse(visitor.onParen(parenNonExclusive, exclusiveLabels));
    }

    @Test
    void testOnNot_WithAtom() {
        LabelExpression.Not notExclusive = new LabelExpression.Not(label1);
        assertFalse(visitor.onNot(notExclusive, exclusiveLabels));
        
        LabelExpression.Not notRegular = new LabelExpression.Not(label3);
        assertTrue(visitor.onNot(notRegular, exclusiveLabels));
    }

    @Test
    void testOnNot_WithExpression() {
        LabelExpression.Paren paren = new LabelExpression.Paren(label1);
        LabelExpression.Not notParen = new LabelExpression.Not(paren);
        assertFalse(visitor.onNot(notParen, exclusiveLabels));
    }

    @Test
    void testOnAnd_BothExclusive() {
        LabelExpression.And and = new LabelExpression.And(label1, label2);
        assertTrue(visitor.onAnd(and, exclusiveLabels));
    }

    @Test
    void testOnAnd_OneExclusive() {
        LabelExpression.And and = new LabelExpression.And(label1, label3);
        assertTrue(visitor.onAnd(and, exclusiveLabels));
    }

    @Test
    void testOnAnd_NoneExclusive() {
        LabelExpression.And and = new LabelExpression.And(label3, new LabelAtom("another"));
        assertFalse(visitor.onAnd(and, exclusiveLabels));
    }

    @Test
    void testOnOr_BothExclusive() {
        LabelExpression.Or or = new LabelExpression.Or(label1, label2);
        assertTrue(visitor.onOr(or, exclusiveLabels));
    }

    @Test
    void testOnOr_OneExclusive() {
        LabelExpression.Or or = new LabelExpression.Or(label1, label3);
        assertTrue(visitor.onOr(or, exclusiveLabels));
    }

    @Test
    void testOnOr_NoneExclusive() {
        LabelExpression.Or or = new LabelExpression.Or(label3, new LabelAtom("another"));
        assertFalse(visitor.onOr(or, exclusiveLabels));
    }

    @Test
    void testOnIff_BothExclusive() {
        LabelExpression.Iff iff = new LabelExpression.Iff(label1, label2);
        assertTrue(visitor.onIff(iff, exclusiveLabels));
    }

    @Test
    void testOnIff_OneExclusive() {
        LabelExpression.Iff iff = new LabelExpression.Iff(label1, label3);
        assertTrue(visitor.onIff(iff, exclusiveLabels));
    }

    @Test
    void testOnIff_NoneExclusive() {
        LabelExpression.Iff iff = new LabelExpression.Iff(label3, new LabelAtom("another"));
        assertFalse(visitor.onIff(iff, exclusiveLabels));
    }

    @Test
    void testOnImplies_ExclusiveConsequent() {
        LabelExpression.Implies implies = new LabelExpression.Implies(label3, label1);
        assertTrue(visitor.onImplies(implies, exclusiveLabels));
    }

    @Test
    void testOnImplies_NonExclusiveConsequent() {
        LabelExpression.Implies implies = new LabelExpression.Implies(label1, label3);
        assertFalse(visitor.onImplies(implies, exclusiveLabels));
    }

    @Test
    void testEmptyExclusiveLabels() {
        Set<LabelAtom> empty = new HashSet<>();
        assertFalse(visitor.onAtom(label1, empty));
        assertFalse(visitor.onAtom(label2, empty));
        assertFalse(visitor.onAtom(label3, empty));
    }
}