package cn.dpc.provision.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ConfigurationDescription {
    private String title;
    private String description;
    private String key;
    private Object data;
    private Object trackingData;
    private StaticStatus staticStatus;
    private TimeRange timeRange;
    private DisplayRule displayRule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long priority;

    public static ConfigurationDescriptionInternalBuilder builder() {
        return new ConfigurationDescriptionInternalBuilder();
    }

    public DynamicStatus getStatus() {
        switch (staticStatus) {

            case DISABLED:
                return DynamicStatus.DISABLED;
            case DELETED:
                return DynamicStatus.DELETED;
            case PUBLISHED:
                boolean notBegin = Optional.ofNullable(timeRange).map(TimeRange::getStartDate)
                        .map(startTime -> startTime.isAfter(LocalDateTime.now()))
                        .orElse(false);
                if (notBegin) {
                    return DynamicStatus.NOT_BEGIN;
                }

                boolean expired = Optional.ofNullable(timeRange).map(TimeRange::getEndDate)
                        .map(endTime -> endTime.isBefore(LocalDateTime.now())).orElse(false);
                if (expired) {
                    return DynamicStatus.EXPIRED;
                }

                return DynamicStatus.IN_PROGRESS;
            case DRAFT:
            default:
                return DynamicStatus.DRAFT;
        }
    }

    public boolean isAvailable() {
        return Optional.ofNullable(timeRange).map(TimeRange::getEndDate)
                .map(endTime -> endTime.isAfter(LocalDateTime.now())).orElse(true)
                && (this.staticStatus == StaticStatus.DRAFT || this.staticStatus == StaticStatus.PUBLISHED);
    }

    public enum StaticStatus {
        DRAFT, PUBLISHED, DISABLED, DELETED
    }

    public static class ConfigurationDescriptionInternalBuilder extends ConfigurationDescriptionBuilder {

        ConfigurationDescriptionInternalBuilder() {
            super();
        }

        @Override
        public ConfigurationDescription build() {
            ConfigurationDescription foo = super.build();
            foo.init();
            return foo;
        }
    }

    private void init() {
        if (null == key || key.trim().length() == 0) {
            key = UUID.randomUUID().toString();
        }

        if (null == staticStatus) {
            staticStatus = StaticStatus.DRAFT;
        }
    }

    public enum DynamicStatus {
        DRAFT, NOT_BEGIN, IN_PROGRESS, EXPIRED, DISABLED, DELETED
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DisplayRule {
        private DisplayRuleType type;
        private int times;
        private List<DailyCondition> dailyConditions;

        public enum DisplayRuleType {
            EVERYTIME,
            FIX_TIMES,
            DAY_FIX_TIMES
        }

        @Data
        public static class DailyCondition {
            private int dayOfWeek;
            private String start;
            private String end;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeRange {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate;
    }
}
