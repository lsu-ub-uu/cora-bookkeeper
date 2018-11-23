package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class LimitsContainerTest {

	@Test
	public void testMinMax() {
		double min = -1;
		double max = 45.5;
		LimitsContainer limits = LimitsContainer.usingMinAndMax(min, max);
		assertEquals(limits.min, min);
		assertEquals(limits.max, max);
	}

}
