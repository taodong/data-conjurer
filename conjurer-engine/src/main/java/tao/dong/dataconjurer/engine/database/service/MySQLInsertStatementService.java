package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.StringValueSupplier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.GROUP_END;
import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.GROUP_START;
import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.NEW_LINE;
import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.QUERY_DELIMITER;
import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.SPACE;
import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.VALUE_DELIMITER;

public class MySQLInsertStatementService implements InsertStatementService{
    private static final String INSERT_STATEMENT = "INSERT INTO";
    private static final String VALUE_STATEMENT = "VALUES";

    @Override
    public StringBuilder generateInsertStatement(@NotNull String entity, @NotNull List<String> properties, @NotNull List<List<StringValueSupplier<String>>> values) {
        StringBuilder queryBuilder = new StringBuilder(100 * values.size());
        for (var rowNum = 0; rowNum < values.size(); rowNum++) {
            var row = values.get(rowNum);
            var skippedIndex = lookupSkippedProperties(row);
            List<String> selectedProperties;
            if (skippedIndex.length > 0) {
                selectedProperties = IntStream.range(0, properties.size())
                        .filter(i -> Arrays.stream(skippedIndex).noneMatch(j -> j == i))
                        .mapToObj(properties::get)
                        .toList();
            } else {
                selectedProperties = properties;
            }

            queryBuilder
                    .append(INSERT_STATEMENT)
                    .append(SPACE.getDelimiter())
                    .append(entity)
                    .append(GROUP_START.getDelimiter())
                    .append(
                            joinValues(VALUE_DELIMITER.getDelimiter(),
                                       selectedProperties.stream()
                                               .map(this::routeToStringMethod)
                                               .toList())
                    )
                    .append(GROUP_END.getDelimiter())
                    .append(SPACE.getDelimiter())
                    .append(VALUE_STATEMENT)
            ;

            queryBuilder.append(SPACE.getDelimiter())
                    .append(GROUP_START)
                    .append(joinValues(VALUE_DELIMITER.getDelimiter(), row))
                    .append(GROUP_END.getDelimiter())
                    .append(QUERY_DELIMITER.getDelimiter());

            if (rowNum < values.size() - 1) {
                queryBuilder.append(NEW_LINE.getDelimiter());
            }
        }

        return queryBuilder;
    }

    private int[] lookupSkippedProperties(List<StringValueSupplier<String>> row) {
        return IntStream.range(0, row.size()).filter(i -> row.get(i) == null).toArray();
    }


}
