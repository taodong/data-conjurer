package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.StringValueSupplier;
import tao.dong.dataconjurer.common.model.TextValue;

import java.util.List;
import java.util.function.Supplier;

public interface InsertStatementService {
    StringBuilder generateInsertStatement(String entity, List<String> properties, List<List<StringValueSupplier>> values);

    default TextValue routeToStringMethod(@NotNull Object o) {
        return new TextValue(o.toString());
    }

    default StringBuilder joinValues(@NotNull char delimiter, @NotNull List<? extends StringValueSupplier> values) {
        return values.stream().map(Supplier::get).collect(
                StringBuilder::new,
                (sb, el) -> sb.append(delimiter).append(el),
                StringBuilder::append).deleteCharAt(0);
    }
}
