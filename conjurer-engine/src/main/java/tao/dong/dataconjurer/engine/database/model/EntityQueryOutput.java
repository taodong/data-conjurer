package tao.dong.dataconjurer.engine.database.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EntityQueryOutput {
    private String entity;
    private int order;
    private StringBuilder queries;
}
