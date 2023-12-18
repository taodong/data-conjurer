package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.CharacterGroup;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.Duration;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityEntry;
import tao.dong.dataconjurer.common.model.EntityIndex;
import tao.dong.dataconjurer.common.model.EntityOutputControl;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.LinkedTypedValue;
import tao.dong.dataconjurer.common.model.NumberCorrelation;
import tao.dong.dataconjurer.common.model.Precision;
import tao.dong.dataconjurer.common.model.PropertyInputControl;
import tao.dong.dataconjurer.common.model.PropertyLink;
import tao.dong.dataconjurer.common.model.PropertyOutputControl;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.common.model.PropertyValueDistribution;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.SecondMark;
import tao.dong.dataconjurer.common.model.SimpleTypedValue;
import tao.dong.dataconjurer.common.model.TypedValue;
import tao.dong.dataconjurer.common.model.UnfixedSize;
import tao.dong.dataconjurer.common.model.ValueCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;
import static tao.dong.dataconjurer.common.model.PropertyType.DATE;
import static tao.dong.dataconjurer.common.model.PropertyType.DATETIME;
import static tao.dong.dataconjurer.common.model.PropertyType.NUMBER;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

public class EntityTestHelper {
    private final V1DataProviderApi dataProviderApi = mock(V1DataProviderApi.class);

    public DataSchema createSimpleTestSchema() {
        return new DataSchema("test1", Set.of(
                createEntityT1(), createEntityT2(), createEntityT3(), createEntityT4()
        ));
    }

    public DataPlan[] createSimpleTestPlans() {
        return new DataPlan[]{
                new DataPlan("plan1", "test1", MYSQL, List.of(
                        createSimpleData(null, null),
                        createSimpleData("t2", 5L),
                        createSimpleData("t3", 8L),
                        createSimpleData("t4", 5L)
                )),
                new DataPlan("plan2", "test1", MYSQL, List.of(
                        createSimpleDataWithId("t3", 9L, 1)
                ))
        };
    }

    public EntityWrapperId createEntityWrapperIdNoOrder(String entityName) {
        return new EntityWrapperId(entityName, 0);
    }

    public void createSimpleBlueprintDataWithReference(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        createSimpleBlueprintData(data, idMap);
        data.get(createEntityWrapperIdNoOrder("t2")).createReferenced(new PropertyLink("t2p0", null));
        data.get(createEntityWrapperIdNoOrder("t3")).createReferenced(new PropertyLink("t3p0", null));
        data.get(createEntityWrapperIdNoOrder("t4")).createReferenced(new PropertyLink("t4p0", null));
    }


    public void createSimpleBlueprintData(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        var wrapper1 = getSimpleEntityWrapper();
        data.put(wrapper1.getId(), wrapper1);
        DataHelper.appendToSetValueInMap(idMap, wrapper1.getEntityName(), wrapper1.getId());

        var wrapper2 = new EntityWrapper(createEntityT2(), new EntityData("t2", 5L, null, null),
                new EntityOutputControl("t2", Set.of(
                        new PropertyOutputControl("t2p0", false, "id"),
                        new PropertyOutputControl("t2p1", true, null)
                )), dataProviderApi);
        data.put(wrapper2.getId(), wrapper2);
        DataHelper.appendToSetValueInMap(idMap, wrapper2.getEntityName(), wrapper2.getId());

        var wrapper3 = new EntityWrapper(createEntityT3(), new EntityData("t3", 5L, null, null), null, dataProviderApi);
        data.put(wrapper3.getId(), wrapper3);
        DataHelper.appendToSetValueInMap(idMap, wrapper3.getEntityName(), wrapper3.getId());

        var wrapper4 = new EntityWrapper(createEntityT4(), new EntityData("t4", 5L, null, null), null, dataProviderApi);
        data.put(wrapper4.getId(), wrapper4);
        DataHelper.appendToSetValueInMap(idMap, wrapper4.getEntityName(), wrapper4.getId());
    }

    public EntityWrapper getSimpleEntityWrapper() {
        return new EntityWrapper(createEntityT1(), createSimpleData(null, null), null, dataProviderApi);
    }

    public DataEntity createEntityT4() {
        return new DataEntity("t4",
                Set.of(
                        entityPropertyBuilder().name("t4p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build()
                )
        );
    }

    public DataEntity createEntityT3() {
        return new DataEntity("t3",
                Set.of(
                        entityPropertyBuilder().name("t3p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t3p1").reference(new Reference("t4", "t4p0", null)).build()
                )
        );
    }

    public DataEntity createEntityT2() {
        return new DataEntity("t2",
                Set.of(
                        entityPropertyBuilder().name("t2p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t2p1").type(TEXT).constraints(List.of(new Length(15L))).build()
                )
        );
    }

    public DataEntity createEntityT1() {
        return new DataEntity("t1",
                Set.of(
                        entityPropertyBuilder().name("t1p1").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t1p2").reference(new Reference("t2", "t2p0", null)).build(),
                        entityPropertyBuilder().name("t1p3").type(TEXT).constraints(List.of(new UnfixedSize(15L))).build(),
                        entityPropertyBuilder().name("t1p4").reference(new Reference("t3", "t3p0", null)).build()
                )
        );
    }

    public EntityData createSimpleData(String entityName, Long count) {
        return new EntityData(entityName == null ? "t1" : entityName,
                count == null ? 10L : count, null, null);
    }

    public EntityData createSimpleDataWithId(String entityName, Long count, int id) {
        return new EntityData(entityName == null ? "t1" : entityName, id, count == null ? 10L : count, null, null);
    }

    public DataEntity createEntityT5() {
        return new DataEntity("t5",
                Set.of(
                        entityPropertyBuilder().name("t5p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t5p1").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t5p2").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t1p3").type(TEXT).constraints(List.of(new UnfixedSize(15L))).build()
                )
        );
    }

    public DataEntity createEntityT6() {
        return new DataEntity("t6",
                Set.of(
                    entityPropertyBuilder().name("t6p0").index(entityIndexBuilder().build()).build(),
                    entityPropertyBuilder().name("t6p1").type(TEXT).constraints(List.of(new UnfixedSize(15L))).build(),
                    entityPropertyBuilder().name("t6p2").type(DATE).build(),
                    entityPropertyBuilder().name("t6p3").type(DATETIME).build(),
                    entityPropertyBuilder().name("t6p4").type(NUMBER).constraints(List.of(new Precision(2))).build(),
                    entityPropertyBuilder().name("t6p5").index(entityIndexBuilder().id(1).type(1).build()).reference(new Reference("t7", "t7p0", null)).build(),
                    entityPropertyBuilder().name("t6p6").index(entityIndexBuilder().id(1).type(1).build()).reference(new Reference("t8", "t8p0", null)).build(),
                    entityPropertyBuilder().name("t6p7").index(entityIndexBuilder().id(2).type(2).qualifier(1).build()).reference(new Reference("t5", "t5p0", "t5p1")).build(),
                    entityPropertyBuilder().name("t6p8").index(entityIndexBuilder().id(2).type(2).qualifier(2).build()).reference(new Reference("t5", "t5p0", "t5p1")).build(),
                    entityPropertyBuilder().name("t6p9").type(TEXT).constraints(List.of(new UnfixedSize(32L), new ValueCategory("name", null, null))).build()
                )
        );
    }

    public Map<Reference, TypedValue> createReferencedT6() {
        var refT6p5 = new SimpleTypedValue(SEQUENCE);
        var refT6p6 = new SimpleTypedValue(SEQUENCE);
        var refT6p7 = new LinkedTypedValue(SEQUENCE, "t5p1");
        IntStream.range(1, 11).mapToLong(Long::valueOf).forEach(l -> {
            refT6p5.addValue(l);
            refT6p6.addValue(l);
        });
        IntStream.range(1, 40).forEach(i -> refT6p7.addLinkedValue(String.valueOf(i % 3), (long)i));

        return Map.of(
                new Reference("t7", "t7p0", null), refT6p5,
                new Reference("t8", "t8p0", null), refT6p6,
                new Reference("t5", "t5p0", "t5p1"), refT6p7
        );
    }

    public EntityData createDataT6() {
        var start = new SecondMark();
        start.setYear(2023);
        var end = new SecondMark();
        end.setYear(2023);
        end.setMonth(5);
        end.setDay(31);
        return entityDataBuilder()
                .entity("t6")
                .count(10)
                .properties(
                        Set.of(
                            PropertyInputControl.builder()
                                    .name("t6p1")
                                    .values(
                                            List.of(new PropertyValueDistribution(Set.of("abc", "efg"), 0.5))
                                    )
                                    .defaultValue("de")
                                    .referenceStrategy("loop")
                                    .constraints(
                                            List.of(new CharacterGroup(Set.of("ASCII_ALPHA_NUMERALS")))
                                    )
                                    .build(),
                            PropertyInputControl.builder()
                                    .name("t6p2")
                                    .constraints(
                                            List.of(
                                                    new Duration(start, end))
                                    )
                                    .build(),
                            PropertyInputControl.builder()
                                    .name("t6p3")
                                    .constraints(
                                            List.of(new NumberCorrelation(Set.of("t6p2"), "PAST_TIME_AFTER(t6p2)"))
                                    )
                                    .build(),
                            PropertyInputControl.builder()
                                    .name("t6p7")
                                    .referenceStrategy("loop")
                                    .build(),
                            PropertyInputControl.builder()
                                    .name("t6p8")
                                    .referenceStrategy("loop")
                                    .build()
                        )
                )
                .entries(new EntityEntry(List.of("t6p1"), List.of(List.of("hello"), List.of("world"))))
                .build();
    }

    public EntityOutputControl createOutputControlT6() {
        return new EntityOutputControl("control-t6", Set.of(
                new PropertyOutputControl("t6p1", false, "t6p1v1"),
                new PropertyOutputControl("t6p8", true, null)
        ));
    }

    public static TestEntityIndexBuilder entityIndexBuilder() {
        return new TestEntityIndexBuilder();
    }

    public static class TestEntityIndexBuilder {
        private int id;
        private int type;
        private int qualifier;

        public TestEntityIndexBuilder id(int id) {
            this.id = id;
            return this;
        }

        public TestEntityIndexBuilder type(int type) {
            this.type = type;
            return this;
        }

        public TestEntityIndexBuilder qualifier(int qualifier) {
            this.qualifier = qualifier;
            return this;
        }

        public EntityIndex build() {
            return new EntityIndex(id, type, qualifier);
        }
    }

    public static TestEntityPropertyBuilder entityPropertyBuilder() {
        return new TestEntityPropertyBuilder();
    }

    public static class TestEntityPropertyBuilder {
        private String name = "test";
        private PropertyType type = SEQUENCE;
        private EntityIndex index = null;
        private List<Constraint<?>> constraints = new ArrayList<>();
        private Reference reference = null;

        public TestEntityPropertyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TestEntityPropertyBuilder type(PropertyType type) {
            this.type = type;
            return this;
        }

        public TestEntityPropertyBuilder index(EntityIndex index) {
            this.index = index;
            return this;
        }

        public TestEntityPropertyBuilder constraints(List<Constraint<?>> constraints) {
            this.constraints = constraints;
            return this;
        }

        public TestEntityPropertyBuilder reference(Reference reference) {
            this.reference = reference;
            return this;
        }

        public EntityProperty build() {
            return new EntityProperty(name, type, index, constraints, reference);
        }

    }

    public static TestEntityDataBuilder entityDataBuilder() {
        return new TestEntityDataBuilder();
    }

    @SuppressWarnings("unused")
    public static class TestEntityDataBuilder {
        private String entity = "test";
        private int dataId = 0;
        private Long count = 1L;
        private Set<PropertyInputControl> properties = null;
        private EntityEntry entries = null;

        public TestEntityDataBuilder entity(String entity) {
            this.entity = entity;
            return this;
        }

        public TestEntityDataBuilder dataId(int dataId) {
            this.dataId = dataId;
            return this;
        }

        public TestEntityDataBuilder count(long count) {
            this.count = count;
            return this;
        }

        public TestEntityDataBuilder properties(Set<PropertyInputControl> properties) {
            this.properties = properties;
            return this;
        }

        public TestEntityDataBuilder entries(EntityEntry entries) {
            this.entries = entries;
            return this;
        }

        public EntityData build() {
            return new EntityData(this.entity, this.dataId, this.count, this.properties, this.entries);
        }
    }



}
