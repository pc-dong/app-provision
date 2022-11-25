package cn.dpc.provision.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRange {
    LocalDateTime startDate;
    LocalDateTime endDate;
}
