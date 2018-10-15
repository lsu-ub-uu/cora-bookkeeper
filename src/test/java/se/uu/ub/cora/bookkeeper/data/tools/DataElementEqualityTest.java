package se.uu.ub.cora.bookkeeper.data.tools;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.bookkeeper.data.tools.DataElementEquality.isEqual;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DataElementEqualityTest {
	private static final String SOME_NAME = "someName";
	private static final String SOME_VALUE = "someValue";
	private static final String SOME_REPEAT_ID = "someRepeatId";
	private static final String SOME_OTHER_REPEAT_ID = "someOtherRepeatId";
	private static final String SOME_ATTRIBUTE_ID = "someAttributeId";
	private static final String SOME_ATTRIBUTE_VALUE = "someAttributeValue";
	private static final String SOME_OTHER_ATTRIBUTE_ID = "someOtherAttributeId";
	private static final String SOME_OTHER_ATTRIBUTE_VALUE = "someOtherAttributeValue";
	private static final String A_SECOND_ATTRIBUTE_ID = "aSecondAttributeId";
	private static final String SOME_CHILD_NAME = "someChildName";
	private static final String SOME_OTHER_CHILD_NAME = "someOtherChildName";
	private static final String DEEP_NAME = "deepName";
	private static final String DEEP_VALUE = "deepValue";

	@Test
	public void testDataGroupIsNotDataAtomic() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicIsNotDataGroup() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicVsIdentical() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataAtomicRightNull() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);

		assertFalse(isEqual(left, (DataAtomic) null));
	}

	@Test
	public void testDataAtomicLeftNull() {
		var right = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);

		assertFalse(isEqual((DataAtomic) null, right));
	}

	@Test
	public void testDataAtomicBothNull() {
		assertTrue(isEqual((DataAtomic) null, (DataAtomic) null));
	}

	@Test
	public void testDataAtomicWithDifferentNameInData() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue("someOtherName", SOME_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithNullNames() {
		var left = DataAtomic.withNameInDataAndValue(null, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue(null, SOME_VALUE);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithLeftNameNull() {
		var left = DataAtomic.withNameInDataAndValue(null, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithRightNameNull() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue(null, SOME_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithNullRepeatIds() {
		var left = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE, null);
		var right = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE, null);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithLeftRepeatIdNull() {
		var left = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE, null);
		var right = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_REPEAT_ID);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithRightRepeatIdNull() {
		var left = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_REPEAT_ID);
		var right = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE, null);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithRepeatIdVsIdentical() {
		var left = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_REPEAT_ID);
		var right = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_REPEAT_ID);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithDifferentRepeatId() {
		var left = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_REPEAT_ID);
		var right = DataAtomic.withNameInDataAndValueAndRepeatId(SOME_NAME, SOME_VALUE,
				SOME_OTHER_REPEAT_ID);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataAtomicWithDifferentValues() {
		var left = DataAtomic.withNameInDataAndValue(SOME_NAME, SOME_VALUE);
		var right = DataAtomic.withNameInDataAndValue(SOME_NAME, "someOtherValue");

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupDifferentNames() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataGroup.withNameInData("someOtherName");

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupRightNull() {
		var left = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual(left, (DataGroup) null));
	}

	@Test
	public void testDataGroupLeftNull() {
		var right = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual((DataGroup) null, right));
	}

	@Test
	public void testDataGroupBothNull() {
		assertTrue(isEqual((DataGroup) null, (DataGroup) null));
	}

	@Test
	public void testDataGroupWithLeftNameNull() {
		var left = DataGroup.withNameInData(null);
		var right = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRightNameNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataGroup.withNameInData(null);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupVsIdentical() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataGroup.withNameInData(SOME_NAME);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithLeftRepeatIdNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.setRepeatId(null);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.setRepeatId(SOME_REPEAT_ID);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRightRepeatIdNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.setRepeatId(SOME_REPEAT_ID);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.setRepeatId(null);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRepeatIdVsIdentical() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.setRepeatId(SOME_REPEAT_ID);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.setRepeatId(SOME_REPEAT_ID);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentRepeatId() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.setRepeatId(SOME_REPEAT_ID);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.setRepeatId(SOME_OTHER_REPEAT_ID);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRightAttributesEmpty() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithLeftAttributesEmpty() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentAttributeIds() {
		assertFalseOnDifferentAttributeIdOrValue(SOME_OTHER_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
	}

	private void assertFalseOnDifferentAttributeIdOrValue(String attributeId,
			String attributeValue) {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, attributeValue);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(attributeId, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentAttributeValues() {
		assertFalseOnDifferentAttributeIdOrValue(SOME_ATTRIBUTE_ID, SOME_OTHER_ATTRIBUTE_VALUE);
	}

	@Test
	public void testDataGroupWithLeftAttributeValueNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, null);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_OTHER_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRightAttributeValueNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, null);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithBothAttributeValuesNull() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, null);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, null);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithTwoAttributesVsIdentical() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		left.addAttributeByIdWithValue(A_SECOND_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(A_SECOND_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWhereLeftHasTwoAttributesAndRightHasOne() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		left.addAttributeByIdWithValue(A_SECOND_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWhereRightHasTwoAttributesAndLeftHasOne() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(A_SECOND_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithTwoAttributesAndOneDifferingId() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		left.addAttributeByIdWithValue(SOME_OTHER_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue("someOtherSecondAttributeId", SOME_ATTRIBUTE_VALUE);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithTwoAttributesAndOneDifferingValue() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		left.addAttributeByIdWithValue(SOME_OTHER_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addAttributeByIdWithValue(SOME_OTHER_ATTRIBUTE_ID, SOME_OTHER_ATTRIBUTE_VALUE);
		right.addAttributeByIdWithValue(SOME_ATTRIBUTE_ID, SOME_ATTRIBUTE_VALUE);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithRightWithoutChildren() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		var right = DataGroup.withNameInData(SOME_NAME);

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithLeftWithoutChildren() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentNumberOfChildren() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		left.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentChildType() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDifferentNameOfSecondChild() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		right.addChild(DataGroup.withNameInData(SOME_OTHER_CHILD_NAME));

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithChildrenOutOfOrder() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		right.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));

		assertFalse(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithMixedChildren() {
		var left = DataGroup.withNameInData(SOME_NAME);
		left.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		left.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));
		var right = DataGroup.withNameInData(SOME_NAME);
		right.addChild(DataGroup.withNameInData(SOME_CHILD_NAME));
		right.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDeepMixedChildren() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var leftChildGroup = DataGroup.withNameInData(SOME_CHILD_NAME);
		leftChildGroup.addChild(DataAtomic.withNameInDataAndValue(DEEP_NAME, DEEP_VALUE));
		left.addChild(leftChildGroup);
		left.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));
		var right = DataGroup.withNameInData(SOME_NAME);
		var rightChildGroup = DataGroup.withNameInData(SOME_CHILD_NAME);
		rightChildGroup.addChild(DataAtomic.withNameInDataAndValue(DEEP_NAME, DEEP_VALUE));
		right.addChild(rightChildGroup);
		right.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));

		assertTrue(isEqual(left, right));
	}

	@Test
	public void testDataGroupWithDeepMixedChildrenWithDifferentRepeatIds() {
		var left = DataGroup.withNameInData(SOME_NAME);
		var leftChildGroup = DataGroup.withNameInData(SOME_CHILD_NAME);
		leftChildGroup.addChild(DataAtomic.withNameInDataAndValueAndRepeatId(DEEP_NAME, DEEP_VALUE,
				SOME_REPEAT_ID));
		left.addChild(leftChildGroup);
		left.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));
		var right = DataGroup.withNameInData(SOME_NAME);
		var rightChildGroup = DataGroup.withNameInData(SOME_CHILD_NAME);
		rightChildGroup.addChild(DataAtomic.withNameInDataAndValueAndRepeatId(DEEP_NAME, DEEP_VALUE,
				SOME_OTHER_REPEAT_ID));
		right.addChild(rightChildGroup);
		right.addChild(DataAtomic.withNameInDataAndValue(SOME_CHILD_NAME, SOME_VALUE));

		assertFalse(isEqual(left, right));
	}

}
