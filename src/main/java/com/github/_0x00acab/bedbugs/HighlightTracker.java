package com.github._0x00acab.bedbugs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

public final class HighlightTracker {
    public static final long HIGHLIGHT_DURATION_MILLIS = 4_000L;
    public static final HighlightTracker INSTANCE = new HighlightTracker();

    private final Map<Integer, Long> expiryByEntityId = new HashMap<>();
    private final LongSupplier clock;

    private HighlightTracker() {
        this(System::currentTimeMillis);
    }

    HighlightTracker(LongSupplier clock) {
        this.clock = clock;
    }

    public void highlight(int entityId) {
        sweepExpired();
        expiryByEntityId.put(entityId, clock.getAsLong() + HIGHLIGHT_DURATION_MILLIS);
    }

    public boolean isHighlighted(int entityId) {
        Long expiry = expiryByEntityId.get(entityId);
        if (expiry == null) {
            return false;
        }
        if (clock.getAsLong() >= expiry) {
            expiryByEntityId.remove(entityId);
            return false;
        }
        return true;
    }

    int trackedCount() {
        return expiryByEntityId.size();
    }

    private void sweepExpired() {
        long now = clock.getAsLong();
        expiryByEntityId.values().removeIf(expiry -> now >= expiry);
    }
}
