package tao.dong.dataconjurer.common.model;

public record Email(String value) implements CompoundValue {
    @Override
    public String getCategory() {
        return "email";
    }
}
