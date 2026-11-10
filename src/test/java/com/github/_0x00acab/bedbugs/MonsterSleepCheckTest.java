package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonsterSleepCheckTest {
	@Test
	void certainCandidatesWinOverUncertainOnes() {
		List<String> all = Arrays.asList("zombie", "calm piglin", "skeleton");

		assertEquals(Arrays.asList("zombie", "skeleton"),
			MonsterSleepCheck.preferCertain(all, name -> !name.contains("piglin")));
	}

	@Test
	void everythingIsReturnedWhenNothingIsCertain() {
		List<String> all = Arrays.asList("calm piglin", "another piglin");

		assertEquals(all, MonsterSleepCheck.preferCertain(all, name -> !name.contains("piglin")));
	}
}
