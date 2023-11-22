package tao.dong.dataconjurer.common.model;

import java.util.List;

public record EntityEntry(List<String> properties, List<List<String>> values) {
}
