package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintTest {

    @Test
    void isMet() {
        // Default implementation should return true
        Constraint<String> defaultConstraint = () -> null;
        assertTrue(defaultConstraint.isMet("anything"));

        // Overridden implementation should return the provided value
        Constraint<String> falseConstraint = new Constraint<>() {
            @Override
            public ConstraintType getType() {
                return null;
            }

            @Override
            public boolean isMet(String val) {
                return false;
            }
        };
        assertFalse(falseConstraint.isMet("anything"));
    }
}