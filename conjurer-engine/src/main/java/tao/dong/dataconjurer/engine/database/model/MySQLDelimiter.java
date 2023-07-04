package tao.dong.dataconjurer.engine.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MySQLDelimiter {

    GROUP_START('('),
    GROUP_END(')'),
    VALUE_DELIMITER(','),
    QUERY_DELIMITER(';'),
    SPACE(' '),
    QUOTE('\'');

    private final char delimiter;

    @Override
    public String toString() {
        return String.valueOf(delimiter);
    }
}
