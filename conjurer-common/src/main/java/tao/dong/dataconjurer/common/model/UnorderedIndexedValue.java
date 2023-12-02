package tao.dong.dataconjurer.common.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnorderedIndexedValue extends UniqueIndex<Set<String>> {

    public UnorderedIndexedValue(int[] indexOrders) {
        super(indexOrders);
    }

    @Override
    public boolean addValue(List<Object> entry) {
        try {
            var val = entryToVal(entry);
            return val.size() == indexOrders.length && values.add(val);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    protected Set<String> entryToVal(List<Object> entry) {
        return Arrays.stream(indexOrders)
                .mapToObj(entry::get)
                .map(VALUE_TO_STRING)
                .collect(Collectors.toSet());
    }

}
