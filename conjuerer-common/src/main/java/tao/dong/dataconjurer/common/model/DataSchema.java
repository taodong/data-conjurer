package tao.dong.dataconjurer.common.model;

import lombok.Getter;

@Getter
public record DataSchema(String name, Dialect dialect) {
}
