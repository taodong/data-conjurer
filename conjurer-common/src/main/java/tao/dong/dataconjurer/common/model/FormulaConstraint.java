package tao.dong.dataconjurer.common.model;

import java.util.Set;

public interface FormulaConstraint {
    Set<String> properties();
    String formula();
}
