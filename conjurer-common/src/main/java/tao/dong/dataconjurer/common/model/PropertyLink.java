package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotBlank;

public record PropertyLink(@NotBlank String name, String linked) {
}
