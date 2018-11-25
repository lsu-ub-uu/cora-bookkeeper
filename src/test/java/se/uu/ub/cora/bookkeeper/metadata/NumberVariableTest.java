package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NumberVariableTest {
	private NumberVariable number;
	private LimitsContainer limits;
	private LimitsContainer warnLimits;
	private int numOfDecimals;
	private StandardMetadataParameters standardParameters;

	@BeforeMethod
	public void setup() {
		TextContainer texts = TextContainer.usingTextIdAndDefTextId("someText", "someDefText");
		standardParameters = StandardMetadataParameters.usingIdNameInDataAndTextContainer("someId",
				"metadata", texts);

		limits = new LimitsContainer(0, 100.4);
		warnLimits = new LimitsContainer(2, 100);
		numOfDecimals = 2;
		number = NumberVariable.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(
				standardParameters, limits, warnLimits, numOfDecimals);
	}

	@Test
	public void testIdAndNameInDatat() {
		assertEquals(number.getId(), "someId");
		assertEquals(number.getNameInData(), "metadata");
	}

	@Test
	public void testTexts() {
		assertEquals(number.getTextId(), "someText");
		assertEquals(number.getDefTextId(), "someDefText");
	}

	@Test
	public void testMinMax() {
		assertEquals(number.getMin(), limits.min);
		assertEquals(number.getMax(), limits.max);

	}

	@Test
	public void testWarningMinMax() {
		assertEquals(number.getWarningMin(), warnLimits.min);
		assertEquals(number.getWarningMax(), warnLimits.max);
	}

	@Test
	public void testNumOfDecimals() {
		assertEquals(number.getNumOfDecmials(), numOfDecimals);
	}
}
