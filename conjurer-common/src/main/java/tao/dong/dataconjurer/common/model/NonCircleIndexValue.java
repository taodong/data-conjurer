package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonCircleIndexValue extends UnorderedIndexedValue{
    private static final CircularDependencyChecker CHECKER = new CircularDependencyChecker();

    @Getter(AccessLevel.PACKAGE)
    private final int parentIndex;
    @Getter(AccessLevel.PACKAGE)
    private final int childIndex;
    @Getter(AccessLevel.NONE)
    private final Map<String, Set<String>> nodes = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Set<String> children = new HashSet<>();


    public NonCircleIndexValue(int[] indexOrders, int parentIndex, int childIndex) {
        super(indexOrders);
        validate(parentIndex, childIndex);
        this.parentIndex = parentIndex;
        this.childIndex = childIndex;
    }

    private void validate(int parentIndex, int childIndex) {
        if (parentIndex < 0 || childIndex < 0 || parentIndex == childIndex) {
            throw new IllegalArgumentException("Invalid index value for non circle index. parent: " + parentIndex + " child: " + childIndex);
        }
    }

    @Override
    public boolean addValue(List<Object> entry) {
        try {
            var parent = VALUE_TO_STRING.apply(entry.get(parentIndex));
            var child = VALUE_TO_STRING.apply(entry.get(childIndex));
            if (children.contains(child)) {
                return false;
            }
            var proposed = new HashMap<>(nodes);
            DataHelper.appendToSetValueInMap(proposed, parent, child);
            if (CHECKER.hasCircular(proposed)) {
                return false;
            }
            var val = entryToVal(entry);
            var res = val.size() == indexOrders.length && values.add(val);
            if (res) {
                DataHelper.appendToSetValueInMap(nodes, parent, child);
                children.add(child);
            }
            return res;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
