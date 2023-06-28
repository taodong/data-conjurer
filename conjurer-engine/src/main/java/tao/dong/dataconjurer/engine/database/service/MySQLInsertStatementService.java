package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.StringValueSupplier;

import java.util.List;

import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.GROUP_END;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.GROUP_START;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.QUERY_DELIMITER;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.SPACE;
import static tao.dong.dataconjurer.engine.database.model.MySqlDelimiter.VALUE_DELIMITER;

public class MySQLInsertStatementService implements InsertStatementService{
    private static final String INSERT_STATEMENT = "INSERT INTO";
    private static final String VALUE_STATEMENT = "VALUES";

    @SuppressWarnings("rawtypes")
    @Override
    public StringBuilder generateInsertStatement(@NotNull String entity, @NotNull List<String> properties, @NotNull List<List<StringValueSupplier>> values) {
        StringBuilder queryBuilder = new StringBuilder(100 * values.size());
        queryBuilder
                .append(INSERT_STATEMENT)
                .append(SPACE.getDelimiter())
                .append(entity)
                .append(GROUP_START.getDelimiter())
                .append(
                        joinValues(VALUE_DELIMITER.getDelimiter(),
                                   properties.stream()
                                           .map(this::routeToStringMethod)
                                           .toList())
                )
                .append(GROUP_END.getDelimiter())
                .append(SPACE.getDelimiter())
                .append(VALUE_STATEMENT)
        ;

        for (var i = 0 ; i < values.size(); i++) {
            queryBuilder.append(SPACE.getDelimiter())
                    .append(GROUP_START)
                    .append(joinValues(VALUE_DELIMITER.getDelimiter(), values.get(i)))
                    .append(GROUP_END.getDelimiter());
            if (i < values.size() -1) {
                queryBuilder.append(VALUE_DELIMITER.getDelimiter());
            }
        }

        queryBuilder.append(QUERY_DELIMITER.getDelimiter());

        return queryBuilder;
    }


}
