package tao.dong.dataconjurer.engine.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MySqlDelimiter {

    GROUP_START('('),
    GROUP_END(')'),
    VALUE_DELIMITER(','),
    QUERY_DELIMITER(';'),
    SPACE(' '),
    QUOTE('\'');

    private final char delimiter;

}
