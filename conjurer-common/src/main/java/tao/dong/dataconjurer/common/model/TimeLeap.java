package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TimeLeap {
    @Size(min = -10, max = 10)
    private int years = 0;
    @Size(min = -12, max = 12)
    private int months = 0;
    @Size(min = -31, max = 31)
    private int days = 0;
    @Size(min = -24, max = 24)
    private int hours = 0;
    @Size(min = -60, max = 60)
    private int minutes = 0;
    @Size(min = -60, max = 60)
    private int seconds = 0;
}
