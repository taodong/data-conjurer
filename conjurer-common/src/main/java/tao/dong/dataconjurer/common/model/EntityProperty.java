package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record EntityProperty(@NotBlank(message = "Property name is required") String name,
                             @NotNull(message = "Property type ${validatedValue} is either missing or unsupported") PropertyType type,
                             @Min(0) int idIndex, List<Constraint<?>> constraints,
                             @Valid Reference reference) {

    public Set<Constraint<?>> getPropertyConstraints() {
        return constraints == null ? Collections.emptySet() : Set.copyOf(constraints);
    }

    public EntityProperty addConstraints(@NotEmpty List<Constraint<?>> extra) {
        return new EntityProperty(name, type, idIndex,
                Stream.concat(DataHelper.streamNullableCollection(constraints), extra.stream()).toList(), reference);
    }
}
