package com.github._0x00acab.bedbugs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

public final class HighlightTracker {
	public static final long HIGHLIGHT_DURATION_MILLIS = 4_000L;
	// How long a bed click waits for the server's refusal; generous for laggy servers.
	public static final long BED_CLICK_WINDOW_MILLIS = 5_000L;
	public static final HighlightTracker INSTANCE = new HighlightTracker();

	private final Map<Integer, Long> expiryByEntityId = new HashMap<>();
	private final LongSupplier clock;
	private Object level;
	private Long clickedBed;
	private long clickedBedExpiry;

	private HighlightTracker() {
		this(() -> System.nanoTime() / 1_000_000L);
	}

	public void ensureLevel(Object level) {
		if (level == null || this.level != level) {
			clear();
			this.level = level;
		}
	}

	public void clear() {
		expiryByEntityId.clear();
		level = null;
		clickedBed = null;
	}

	public void rememberBedClick(long bedPos) {
		clickedBed = bedPos;
		clickedBedExpiry = clock.getAsLong() + BED_CLICK_WINDOW_MILLIS;
	}

	/** The last clicked bed, once, and only while the click is recent. */
	public Long takeBedClick() {
		Long bed = clickedBed;
		clickedBed = null;
		return bed != null && clock.getAsLong() < clickedBedExpiry ? bed : null;
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
