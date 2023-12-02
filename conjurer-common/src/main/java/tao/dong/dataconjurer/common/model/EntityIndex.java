package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.Min;

public record EntityIndex(@Min(0) int id, @Min(0) int type, @Min(0) int qualifier) {
}
