package cn.dpc.provision.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayRule {
    private DisplayRuleType type;
    private int times;
    private List<DailyCondition> dailyConditions;

    public static enum DisplayRuleType {
        EVERYTIME,
        FIX_TIMES,
        DAY_FIX_TIMES;
    }
}
