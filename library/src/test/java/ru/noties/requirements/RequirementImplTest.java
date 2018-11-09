package ru.noties.requirements;

import org.junit.Test;

public class RequirementImplTest {

    @Test
    public void validate_triggers_event_source_subscription() {
        throw null;
    }

    @Test
    public void validate_when_in_progress_does_not_trigger_event_source_subscription() {
        // but both will be notified
        throw null;
    }

    @Test
    public void single_case() {
        throw null;
    }

    @Test
    public void multiple_cases() {
        throw null;
    }

    @Test
    public void is_valid_attach_detaches_no_subscription() {
        // only one case will be attached at a time
        // each one will go through attach/detach
        // no event source subscription will be triggered
        // first false breaks (no cases will be queried after first failure)
        throw null;
    }

    @Test
    public void cancel_detaches_notifies_failure_unsubscribes() {
        throw null;
    }

    @Test
    public void activity_destroyed_detach_unsubscribe_cleanup() {
        throw null;
    }

    @Test
    public void on_requirement_result_when_no_pending_throws() {
        throw null;
    }

    @Test
    public void on_requirement_result_success_next() {
        throw null;
    }
}