package tao.dong.dataconjurer.common.model;

import java.util.List;

public record DataPlan(String name, String schema, List<EntityData> data) {
}
