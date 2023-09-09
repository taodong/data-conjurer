package tao.dong.dataconjurer.engine.database.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.TextValue;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

class InsertStatementServiceTest {
    private final InsertStatementService insertStatementService = spy(InsertStatementService.class);

    @Test
    void testJoinValues() {
        char delimiter = ',';
        List<TextValue> texts = Arrays.asList(new TextValue("abc"), null, new TextValue("efg"), new TextValue("hjk"));
        var result = insertStatementService.joinValues(delimiter, texts);
        assertEquals("abc,efg,hjk", result.toString());
    }

    @Test
    void testRouteToStringMethod() {
        var supplier = insertStatementService.routeToStringMethod("abc");
        assertEquals("abc", supplier.get());
    }
}
