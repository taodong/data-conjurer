package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyValue;

import java.util.List;
import java.util.stream.Collectors;

public class MySQLInsertStatementService implements InsertStatementService{
    private static final String INSERT_STATEMENT = "INSERT INTO";
    private static final String VALUE_STATEMENT = "VALUES";
    private static final char GROUP_START = '(';
    private static final char GROUP_END = ')';
    private static final char VALUE_DELIMITER = ',';
    private static final char QUERY_DELIMITER = ';';
    private static final char WHITE_SPACE = ' ';

    @Override
    public StringBuilder generateInsertStatement(@NotNull String entity, @NotNull List<String> properties, @NotNull List<List<PropertyValue>> values) {
        StringBuilder queryBuilder = new StringBuilder(100 * values.size());
        queryBuilder
                .append(INSERT_STATEMENT)
                .append(WHITE_SPACE)
                .append(entity)
                .append(WHITE_SPACE)
                .append(VALUE_STATEMENT)
                .append(WHITE_SPACE)
                .append(GROUP_START)
                .append(
                        joinValues(VALUE_DELIMITER,
                                   properties.stream()
                                           .map(this::routeToStringMethod)
                                           .collect(Collectors.toList()))
                )
                .append(GROUP_END);

        for (var row : values) {
            queryBuilder.append(WHITE_SPACE)
                    .append(GROUP_START)
                    .append(joinValues(VALUE_DELIMITER, row))
                    .append(GROUP_END);
        }

        queryBuilder.append(QUERY_DELIMITER);

        return queryBuilder;
    }


}
