package se.uu.ub.cora.bookkeeper.data.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DataElementEquality {
	private DataElementEquality() {
	}

	@SuppressWarnings("unused")
	public static boolean isEqual(DataGroup ignoredDataGroup, DataAtomic ignoredDataAtomic) {
		return false;
	}

	@SuppressWarnings("unused")
	public static boolean isEqual(DataAtomic ignoredDataAtomic, DataGroup ignoredDataGroup) {
		return false;
	}

	public static boolean isEqual(DataAtomic left, DataAtomic right) {
		return bothAreNull(left, right) || theyHaveEqualDataElementAndEqualValues(left, right);
	}

	private static boolean bothAreNull(Object left, Object right) {
		return left == null && right == null;
	}

	private static boolean theyHaveEqualDataElementAndEqualValues(DataAtomic left,
			DataAtomic right) {
		return theyHaveEqualDataElements(left, right) && theyHaveEqualValues(left, right);
	}

	private static boolean theyHaveEqualDataElements(DataElement left, DataElement right) {
		return haveEqualNames(left, right) && theyHaveEqualRepeatIds(left, right)
				&& theyHaveEqualAttributes(left, right);
	}

	private static boolean haveEqualNames(DataElement left, DataElement right) {
		return neitherIsNull(left, right) && theyHaveEqualNameInData(left, right);
	}

	private static boolean neitherIsNull(DataElement left, DataElement right) {
		return left != null && right != null;
	}

	private static boolean theyHaveEqualNameInData(DataElement left, DataElement right) {
		String leftNameInData = left.getNameInData();
		String rightNameInData = right.getNameInData();
		return equalStringsWithNullEquality(leftNameInData, rightNameInData);
	}

	private static boolean equalStringsWithNullEquality(String left, String right) {
		return bothAreNull(left, right) || neitherIsNullAndTheyAreEqual(left, right);
	}

	private static boolean neitherIsNullAndTheyAreEqual(String left, String right) {
		return left != null && left.equals(right);
	}

	private static boolean theyHaveEqualRepeatIds(DataElement left, DataElement right) {
		String leftRepeatId = left.getRepeatId();
		String rightRepeatId = right.getRepeatId();
		return equalStringsWithNullEquality(leftRepeatId, rightRepeatId);
	}

	private static boolean theyHaveEqualAttributes(DataElement left, DataElement right) {
		var leftAttributes = left.getAttributes();
		var rightAttributes = right.getAttributes();
		return bothAreEmpty(leftAttributes, rightAttributes)
				|| neitherIsEmptyAndTheyHaveIdenticalAttributes(leftAttributes, rightAttributes);
	}

	private static boolean bothAreEmpty(Map left, Map right) {
		return left.isEmpty() && right.isEmpty();
	}

	private static boolean neitherIsEmptyAndTheyHaveIdenticalAttributes(Map<String, String> left,
			Map<String, String> right) {
		return neitherIsEmpty(left, right) && theyHaveIdenticalAttributes(left, right);
	}

	private static boolean neitherIsEmpty(Map left, Map right) {
		return !left.isEmpty() && !right.isEmpty();
	}

	private static boolean theyHaveIdenticalAttributes(Map<String, String> left,
			Map<String, String> right) {
		return bothAreTheSameSize(left, right)
				&& theyHaveEqualAttributeCollectionsOfSameNonZeroSize(left, right);
	}

	private static boolean bothAreTheSameSize(Map left, Map right) {
		return left.size() == right.size();
	}

	private static boolean theyHaveEqualAttributeCollectionsOfSameNonZeroSize(
			Map<String, String> left, Map<String, String> right) {
		var leftSortedAttributesIterator = getCollectionAsSortedList(left.entrySet());
		var rightSortedAttributesIterator = getCollectionAsSortedList(right.entrySet());
		return leftSortedAttributesIterator.equals(rightSortedAttributesIterator);
	}

	private static List<Map.Entry<String, String>> getCollectionAsSortedList(
			Collection<Map.Entry<String, String>> collection) {
		var list = new ArrayList<>(collection);
		list.sort(Comparator.comparing(Map.Entry::getKey));
		return list;
	}

	private static boolean theyHaveEqualValues(DataAtomic left, DataAtomic right) {
		String leftValue = left.getValue();
		String rightValue = right.getValue();
		return equalStringsWithNullEquality(leftValue, rightValue);
	}

	public static boolean isEqual(DataGroup left, DataGroup right) {
		return bothAreNull(left, right) || theyHaveEqualDataElementAndEqualChildren(left, right);
	}

	private static boolean theyHaveEqualDataElementAndEqualChildren(DataGroup left,
			DataGroup right) {
		return theyHaveEqualDataElements(left, right) && theyHaveEqualChildren(left, right);
	}

	private static boolean theyHaveEqualChildren(DataGroup left, DataGroup right) {
		List<DataElement> leftChildren = left.getChildren();
		List<DataElement> rightChildren = right.getChildren();
		return bothAreEmpty(leftChildren, rightChildren)
				|| neitherIsEmptyAndBothHaveIdenticalChildren(leftChildren, rightChildren);
	}

	private static boolean bothAreEmpty(Collection left, Collection right) {
		return left.isEmpty() && right.isEmpty();
	}

	private static boolean neitherIsEmptyAndBothHaveIdenticalChildren(List<DataElement> left,
			List<DataElement> right) {
		return neitherIsEmpty(left, right) && bothAreSameSizeAndHaveIdenticalChildren(left, right);
	}

	private static boolean neitherIsEmpty(Collection left, Collection right) {
		return !left.isEmpty() && !right.isEmpty();
	}

	private static boolean bothAreSameSizeAndHaveIdenticalChildren(List<DataElement> left,
			List<DataElement> right) {
		return bothAreTheSameSize(left, right) && haveIdenticalChildren(left, right);
	}

	private static boolean bothAreTheSameSize(Collection left, Collection right) {
		return left.size() == right.size();
	}

	private static boolean haveIdenticalChildren(List<DataElement> left, List<DataElement> right) {
		var rightChildIterator = right.iterator();
		return left.stream()
			.allMatch(leftChild -> isEqual(rightChildIterator.next(), leftChild));
	}

	public static boolean isEqual(DataElement left, DataElement right) {
		return bothAreDataAtomicAndEqual(left, right) || bothAreDataGroupAndEqual(left, right);
	}

	private static boolean bothAreDataAtomicAndEqual(DataElement left, DataElement right) {
		return bothAreDataAtomic(left, right) && isEqual((DataAtomic) left, (DataAtomic) right);
	}

	private static boolean bothAreDataAtomic(DataElement left, DataElement right) {
		return left instanceof DataAtomic && right instanceof DataAtomic;
	}

	private static boolean bothAreDataGroupAndEqual(DataElement left, DataElement right) {
		return bothAreDataGroup(left, right) && isEqual((DataGroup) left, (DataGroup) right);
	}

	private static boolean bothAreDataGroup(DataElement left, DataElement right) {
		return left instanceof DataGroup && right instanceof DataGroup;
	}
}
