package cn.dpc.provision.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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

   public DynamicStatus getStatus() {
      switch (staticStatus) {

         case DISABLED: return DynamicStatus.DISABLED;
         case DELETED: return DynamicStatus.DELETED;
         case PUBLISHED:
            boolean notBegin = Optional.ofNullable(timeRange).map(TimeRange::getStartDate)
                    .map(startTime -> startTime.isAfter(LocalDateTime.now()))
                    .orElse(false);
            if(notBegin) {
               return DynamicStatus.NOT_BEGIN;
            }

            boolean expired = Optional.ofNullable(timeRange).map(TimeRange::getEndDate)
                    .map(endTime -> endTime.isBefore(LocalDateTime.now())).orElse(false);
            if(expired) {
               return DynamicStatus.EXPIRED;
            }

            return DynamicStatus.IN_PROGRESS;
         case DRAFT:
         default: return DynamicStatus.DRAFT;
      }
   }
}
