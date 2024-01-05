package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityIndex;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.engine.database.support.MySQLMutableSequenceGenerator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MySQLEntityWrapperTest {

    @Test
    void testIndexGenerator() {
        DataProviderService dataProviderService = mock(DataProviderService.class);
        var entity = new DataEntity("t1", Set.of(
                new EntityProperty("p0", PropertyType.SEQUENCE, new EntityIndex(0, 0, 0), null,null)
        ));

        var data = new EntityData("t1", 10L, null, null);
        var wrapper = new MySQLEntityWrapper(entity, data, null, dataProviderService, 0);
        assertTrue(wrapper.getGenerators().get("p0") instanceof MySQLMutableSequenceGenerator);
    }

}
