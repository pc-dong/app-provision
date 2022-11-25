package cn.dpc.provision.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationDescriptionTest {

    @Test
    public void should_return_draft_when_static_status_is_draft() {
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.DRAFT).build();
        assertEquals(DynamicStatus.DRAFT, description.getStatus());
    }

    @Test
    public void should_return_disabled_when_static_status_is_disabled() {
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.DISABLED).build();
        assertEquals(DynamicStatus.DISABLED, description.getStatus());
    }

    @Test
    public void should_return_deleted_when_static_status_is_deleted() {
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.DELETED).build();
        assertEquals(DynamicStatus.DELETED, description.getStatus());
    }

    @Test
    public void should_return_not_begin_when_static_status_is_public_and_starttime_is_after_now() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.PUBLISHED)
                .timeRange(new TimeRange(startTime, null))
                .build();

        assertEquals(DynamicStatus.NOT_BEGIN, description.getStatus());
    }

    @Test
    public void should_return_expired_when_static_status_is_published_and_end_time_is_before_now() {
        LocalDateTime endTime = LocalDateTime.now().minusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.PUBLISHED)
                .timeRange(new TimeRange(null, endTime))
                .build();

        assertEquals(DynamicStatus.EXPIRED, description.getStatus());
    }

    @Test
    public void should_return_in_progress_when_static_status_is_published_and_time_inprogess() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        var description = ConfigurationDescription.builder().staticStatus(StaticStatus.PUBLISHED)
                .timeRange(new TimeRange(startTime, endTime))
                .build();

        assertEquals(DynamicStatus.IN_PROGRESS, description.getStatus());
    }
}