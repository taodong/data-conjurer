package tao.dong.dataconjurer.engine.database;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyValue;

import java.util.List;

public class MySQLInsertStatementService implements InsertStatementService{
    private static final String INSERT_STATEMENT = "INSERT INTO";
    private static final String VALUE_STATEMENT = "VALUES";
    private static final char GROUP_START = '(';
    private static final char GROUP_END = ')';
    private static final char VALUE_DELIMITER = ',';
    private static final char QUERY_DELIMITER = ';';
    private static final char WHITE_SPACE = ' ';

    @Override
    public String generateInsertStatement(@NotNull String entity, @NotNull List<String> properties, @NotNull List<List<PropertyValue>> values) {
        StringBuilder queryBuilder = new StringBuilder(INSERT_STATEMENT);
        queryBuilder.append(' ')
                .append(entity)
                .append(' ')
                .append(VALUE_STATEMENT)
                .append(' ')
                .append(GROUP_START)
                ;

        return null;
    }


}
