package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NonCircleIndexValue extends UnorderedIndexedValue{
    private static final CircularDependencyChecker CHECKER = new CircularDependencyChecker();
    @Getter(AccessLevel.NONE)
    private final int parentIndex;
    @Getter(AccessLevel.NONE)
    private final int childIndex;
    @Getter(AccessLevel.NONE)
    private final Map<String, Set<String>> nodes = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Set<String> children = new HashSet<>();
    @Getter
    private final boolean ordered;


    public NonCircleIndexValue(int[] indexOrders, int parentIndex, int childIndex, boolean ordered) {
        super(indexOrders);
        validate(parentIndex, childIndex);
        this.parentIndex = parentIndex;
        this.childIndex = childIndex;
        this.ordered = ordered;
    }

    private void validate(int parentIndex, int childIndex) {
        if (parentIndex < 0 || childIndex < 0 || parentIndex == childIndex) {
            throw new IllegalArgumentException("Invalid index value for non circle index. parent: " + parentIndex + " child: " + childIndex);
        }
    }

    @Override
    public boolean addValue(List<Object> entry) {
        try {
            if (ordered) {
                enforceOrder(entry); // Switch values of parent and child to make sure parent value is less or equal to child
            }
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

    @SuppressWarnings("rawtypes, unchecked") // Explicitly suppress raw type and unchecked due to unknown type of entry elements
    void enforceOrder(List<Object> entry) {
        var parent = entry.get(parentIndex);
        var child = entry.get(childIndex);
        try {
            if (parent instanceof Comparable pv && child instanceof Comparable cv && pv.compareTo(cv) > 0) {
                Collections.swap(entry, parentIndex, childIndex);
            }
        } catch (Exception e) {
            LOG.warn("Failed to compare values of parent {} and child {} of ordered non circular index", VALUE_TO_STRING.apply(parent), VALUE_TO_STRING.apply(child));
        }
    }
}
