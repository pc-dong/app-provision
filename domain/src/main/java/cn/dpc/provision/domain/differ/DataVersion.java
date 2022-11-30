package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataVersion {
    private String version;

    private LoginStatus loginStatus;

    public static final DataVersion empty = new DataVersion();

    @Override
    public String toString() {
        return version + "_" + loginStatus.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataVersion that = (DataVersion) o;
        return Objects.equals(version, that.version) && loginStatus == that.loginStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, loginStatus);
    }

    public static DataVersion parseDataVersion(String dataVersionStr) {
        if (StringUtils.isEmpty(dataVersionStr)) {
            return empty;
        }

        String[] array = dataVersionStr.split("_");

        DataVersion dataVersion = new DataVersion();
        dataVersion.setVersion(array[0]);
        dataVersion.setLoginStatus(array.length > 1 ? LoginStatus.getByValue(Integer.parseInt(array[1])) : null);
        return dataVersion;
    }

    public enum LoginStatus {
        LOGIN_IN(1) , LOGIN_OUT(0);

        private int value;

        LoginStatus(int value) {
            this.value = value;
        }

        public static LoginStatus getByCustomerId(Customer.CustomerId customerId) {
            return null != customerId && StringUtils.isNotEmpty(customerId.getId()) ? LOGIN_IN : LOGIN_OUT;
        }

        public int getValue() {
            return this.value;
        }

        public static LoginStatus getByValue(int value) {
            return Arrays.stream(LoginStatus.values())
                    .filter(ele -> ele.getValue() == value)
                    .findFirst()
                    .orElseGet(null);
        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeVersionObject {

        private LocalDateTime lastUpdateTime;

        private LocalDateTime lastStartTime;

        public static TimeVersionObject create(LocalDateTime lastUpdateTime, LocalDateTime lastStartTime) {
            return TimeVersionObject.builder()
                    .lastUpdateTime(lastUpdateTime)
                    .lastStartTime(lastStartTime)
                    .build();
        }

        public static TimeVersionObject from(String version) {
            String[] lastUpdateTimeWithStartTime = Optional.ofNullable(version)
                    .orElse("")
                    .split("-");

            return TimeVersionObject.builder()
                    .lastUpdateTime(lastUpdateTimeWithStartTime.length > 0 && StringUtils.isNotBlank(lastUpdateTimeWithStartTime[0])
                            ? convertToLocalDateTime(lastUpdateTimeWithStartTime[0])
                            : null)
                    .lastStartTime(lastUpdateTimeWithStartTime.length > 1 && StringUtils.isNotBlank(lastUpdateTimeWithStartTime[1])
                            ? convertToLocalDateTime(lastUpdateTimeWithStartTime[1])
                            : null)
                    .build();
        }

        public String toString() {
            String lastUpdateTimeStr = null == lastUpdateTime ? "" : String.valueOf(lastUpdateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
            String lastStartTimeStr = null == lastStartTime ? "" : "-" + lastStartTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            return lastUpdateTimeStr + lastStartTimeStr;
        }

        public boolean hasUpdated(LocalDateTime currentUpdateTime, LocalDateTime currentStartTime) {
            LocalDateTime lastUpdateTime = this.getLastUpdateTime();
            LocalDateTime lastStartTime = this.getLastStartTime();
            return lastUpdateTime == null
                    || lastUpdateTime.isBefore(currentUpdateTime)
                    || (lastStartTime == null && currentStartTime != null)
                    || (currentStartTime != null && currentStartTime.isAfter(lastStartTime));
        }

        private static LocalDateTime convertToLocalDateTime(String version) {
            try {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(version)), ZoneOffset.UTC);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}

