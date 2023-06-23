package tao.dong.dataconjurer.engine.database;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyValue;

import java.util.List;
import java.util.function.Supplier;

public interface InsertStatementService {
    String generateInsertStatement(String entity, List<String> properties, List<List<PropertyValue>> values);

    default Supplier<String> routeToStringMethod(@NotNull Object o) {
        return () -> o.toString();
    }

    default StringBuilder joinValues(@NotNull char delimiter, @NotNull List<Supplier<String>> values) {
        return values.stream().map(Supplier::get).collect(
                StringBuilder::new,
                (sb, el) -> sb.append(delimiter).append(el),
                StringBuilder::append).deleteCharAt(0);
    }
}
