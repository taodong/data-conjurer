package tao.dong.dataconjurer.engine.database.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.TextValue;
import tao.dong.dataconjurer.engine.database.service.InsertStatementService;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

class InsertStatementServiceTest {
    private InsertStatementService insertStatementService = spy(InsertStatementService.class);

    @Test
    void testJoinValues() {
        char delimiter = ',';
        List<TextValue> texts = List.of(new TextValue("abc"), new TextValue("efg"), new TextValue("hjk"));
        var result = insertStatementService.joinValues(delimiter, texts);
        assertEquals("abc,efg,hjk", result.toString());
    }

    @Test
    void testRouteToStringMethod() {
        var supplier = insertStatementService.routeToStringMethod("abc");
        assertEquals("abc", supplier.get());
    }
}
