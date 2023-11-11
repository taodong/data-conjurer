package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SecondMark {
    @Size(min = 1000)
    private int year;
    @Size(min = 1, max = 12)
    private int month = 1;
    @Size(min = 1, max = 31)
    private int day = 1;
    @Size(max = 23)
    private int hour;
    @Size(max = 59)
    private int minute;
    @Size(max = 59)
    private int second;
}
