package tao.dong.dataconjurer.engine.database.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.engine.database.model.MySQLEntityWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class MySQLDataPlanServiceTest {

    @Test
    void testCreateEntityWrapper() {
        DataProviderService dataProviderService = mock(DataProviderService.class);
        var dataPlanService = new MySQLDataPlanService(dataProviderService);
        try (@SuppressWarnings("unused") MockedConstruction<MySQLEntityWrapper> mockedEntityWrapper =
                mockConstruction(MySQLEntityWrapper.class, (mock, context) -> when(mock.getMsg()).thenReturn("test"))) {
            var entityWrapper = dataPlanService.createEntityWrapper(mock(DataEntity.class), mock(EntityData.class), null, 1);
            assertEquals("test", entityWrapper.getMsg());
        }
    }
}
