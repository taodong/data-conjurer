package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class EntityDataOutput {
    private final String entityName;
    private final List<PropertyType> propertyTypes;
    private final List<String> properties;
    private final List<List<Object>> values = new ArrayList<>();

    public void addValues(List<List<Object>> records) {
        values.addAll(records);
    }
}
