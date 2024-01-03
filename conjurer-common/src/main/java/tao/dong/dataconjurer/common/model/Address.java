package tao.dong.dataconjurer.common.model;

public record Address(String value, String street, String city, String state, String zip, String country) implements CompoundValue{
    @Override
    public String getCategory() {
        return "address";
    }
}
