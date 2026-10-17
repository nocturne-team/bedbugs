package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighlightTrackerTest {

    @Test
    void sameLevelPreservesHighlights() {
        HighlightTracker tracker = new HighlightTracker(() -> 0L);
        Object level = new Object();
        tracker.ensureLevel(level);
        tracker.highlight(42);

        tracker.ensureLevel(level);

        assertTrue(tracker.isHighlighted(42));
    }

    @Test
    void worldReplacementClearsHighlightsBeforeAnotherBedClick() {
        HighlightTracker tracker = new HighlightTracker(() -> 0L);
        // Equal but distinct worlds must still be treated as a world change.
        tracker.ensureLevel(new String("world"));
        tracker.highlight(42);

        tracker.ensureLevel(new String("world"));

        assertFalse(tracker.isHighlighted(42));
        assertEquals(0, tracker.trackedCount());
        tracker.highlight(42);
        assertTrue(tracker.isHighlighted(42));
    }

    @Test
    void unloadClearsHighlightsAndReleasesLevel() throws ReflectiveOperationException {
        HighlightTracker tracker = new HighlightTracker(() -> 0L);
        tracker.ensureLevel(new Object());
        tracker.highlight(42);

        tracker.ensureLevel(null);

        assertEquals(0, tracker.trackedCount());
        var levelField = HighlightTracker.class.getDeclaredField("level");
        levelField.setAccessible(true);
        assertNull(levelField.get(tracker));

        tracker.ensureLevel(new Object());
        assertFalse(tracker.isHighlighted(42));
    }

    @Test
    void nullLevelClearsHighlightsEvenWithoutBoundLevel() {
        HighlightTracker tracker = new HighlightTracker(() -> 0L);
        tracker.highlight(42);

        tracker.ensureLevel(null);

        assertEquals(0, tracker.trackedCount());
    }

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
