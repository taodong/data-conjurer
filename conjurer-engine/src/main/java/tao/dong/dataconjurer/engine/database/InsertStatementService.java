package tao.dong.dataconjurer.engine.database;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyValue;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface InsertStatementService {
    String generateInsertStatement(String entity, List<String> properties, List<List<PropertyValue>> values);

    default Supplier<String> routeToStringMethod(@NotNull Object o) {
        return () -> o.toString();
    }

    default StringBuilder joinValues(char delimiter, @NotNull Collection<Supplier<String>> values) {
        return values.stream().collect(StringBuilder::new, StringBuilder::append, (partial, res) -> res.append(delimiter).append(partial));
    }
}
