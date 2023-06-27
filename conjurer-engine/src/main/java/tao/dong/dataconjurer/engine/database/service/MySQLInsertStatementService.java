package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyValue;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.GROUP_END;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.GROUP_START;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.QUERY_DELIMITER;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.SPACE;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.VALUE_DELIMITER;

public class MySQLInsertStatementService implements InsertStatementService{
    private static final String INSERT_STATEMENT = "INSERT INTO";
    private static final String VALUE_STATEMENT = "VALUES";

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public StringBuilder generateInsertStatement(@NotNull String entity, @NotNull List<String> properties, @NotNull List<List<PropertyValue>> values) {
        StringBuilder queryBuilder = new StringBuilder(100 * values.size());
        queryBuilder
                .append(INSERT_STATEMENT)
                .append(SPACE.getDelimiter())
                .append(entity)
                .append(SPACE.getDelimiter())
                .append(VALUE_STATEMENT)
                .append(SPACE.getDelimiter())
                .append(GROUP_START.getDelimiter())
                .append(
                        joinValues(VALUE_DELIMITER.getDelimiter(),
                                   properties.stream()
                                           .map(this::routeToStringMethod)
                                           .collect(Collectors.toList()))
                )
                .append(GROUP_END.getDelimiter());

        for (var row : values) {
            queryBuilder.append(SPACE.getDelimiter())
                    .append(GROUP_START)
                    .append(joinValues(VALUE_DELIMITER.getDelimiter(), (List<? extends Supplier<String>>) row))
                    .append(GROUP_END.getDelimiter());
        }

        queryBuilder.append(QUERY_DELIMITER.getDelimiter());

        return queryBuilder;
    }


}
