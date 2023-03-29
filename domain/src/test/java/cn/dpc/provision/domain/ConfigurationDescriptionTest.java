package cn.dpc.provision.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationDescriptionTest {

    @Test
    public void should_return_draft_when_static_status_is_draft() {
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.DRAFT).build();
        assertEquals(ConfigurationDescription.DynamicStatus.DRAFT, description.getStatus());
    }

    @Test
    public void should_return_disabled_when_static_status_is_disabled() {
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.DISABLED).build();
        assertEquals(ConfigurationDescription.DynamicStatus.DISABLED, description.getStatus());
    }

    @Test
    public void should_return_deleted_when_static_status_is_deleted() {
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.DELETED).build();
        assertEquals(ConfigurationDescription.DynamicStatus.DELETED, description.getStatus());
    }

    @Test
    public void should_return_not_begin_when_static_status_is_public_and_starttime_is_after_now() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.PUBLISHED)
                .timeRange(new ConfigurationDescription.TimeRange(startTime, null))
                .build();

        assertEquals(ConfigurationDescription.DynamicStatus.NOT_BEGIN, description.getStatus());
    }

    @Test
    public void should_return_expired_when_static_status_is_published_and_end_time_is_before_now() {
        LocalDateTime endTime = LocalDateTime.now().minusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.PUBLISHED)
                .timeRange(new ConfigurationDescription.TimeRange(null, endTime))
                .build();

        assertEquals(ConfigurationDescription.DynamicStatus.EXPIRED, description.getStatus());
    }

    @Test
    public void should_return_in_progress_when_static_status_is_published_and_time_inprogess() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(ConfigurationDescription.StaticStatus.PUBLISHED)
                .timeRange(new ConfigurationDescription.TimeRange(startTime, endTime))
                .build();

        assertEquals(ConfigurationDescription.DynamicStatus.IN_PROGRESS, description.getStatus());
    }
}