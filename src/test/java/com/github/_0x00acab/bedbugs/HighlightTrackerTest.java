package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighlightTrackerTest {

    @Test
    void highlightedEntityIsHighlightedImmediately() {
        long[] now = {1_000L};
        HighlightTracker tracker = new HighlightTracker(() -> now[0]);

        tracker.highlight(42);

        assertTrue(tracker.isHighlighted(42));
    }

    @Test
    void neverHighlightedEntityIsNotHighlighted() {
        HighlightTracker tracker = new HighlightTracker(() -> 1_000L);

        assertFalse(tracker.isHighlighted(99));
    }

    @Test
    void highlightExpiresAfterDuration() {
        long[] now = {0L};
        HighlightTracker tracker = new HighlightTracker(() -> now[0]);
        tracker.highlight(7);

        now[0] = HighlightTracker.HIGHLIGHT_DURATION_MILLIS;

        assertFalse(tracker.isHighlighted(7));
    }

    @Test
    void reHighlightingRefreshesExpiryInsteadOfShorteningIt() {
        long[] now = {0L};
        HighlightTracker tracker = new HighlightTracker(() -> now[0]);
        tracker.highlight(7);

        now[0] = 3_000L;
        tracker.highlight(7);
        now[0] = 3_000L + HighlightTracker.HIGHLIGHT_DURATION_MILLIS - 1;

        assertTrue(tracker.isHighlighted(7));
    }

    @Test
    void expiredEntriesAreSweptRatherThanAccumulating() {
        long[] now = {0L};
        HighlightTracker tracker = new HighlightTracker(() -> now[0]);
        tracker.highlight(1);

        now[0] = HighlightTracker.HIGHLIGHT_DURATION_MILLIS + 1;
        tracker.highlight(2); // triggers the opportunistic sweep of entity 1's expired entry

        assertFalse(tracker.isHighlighted(1));
        assertTrue(tracker.isHighlighted(2));
        assertTrue(tracker.trackedCount() <= 1);
    }
}
