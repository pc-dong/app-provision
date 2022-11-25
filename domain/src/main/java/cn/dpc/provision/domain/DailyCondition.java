package cn.dpc.provision.domain;

import lombok.Data;

@Data
public class DailyCondition {
    private int dayOfWeek;
    private String start;
    private String end;
}
