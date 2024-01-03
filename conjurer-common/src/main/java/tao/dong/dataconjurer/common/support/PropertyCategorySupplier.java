package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.ValueCategory;

public class PropertyCategorySupplier implements ValueGenerator<ValueCategory>{
    private final ValueCategory category;

    public PropertyCategorySupplier(ValueCategory category) {
        this.category = category;
    }

    @Override
    public ValueCategory generate() {
        return category;
    }
}
