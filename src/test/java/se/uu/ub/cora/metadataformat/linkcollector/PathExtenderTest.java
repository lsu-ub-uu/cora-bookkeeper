package se.uu.ub.cora.metadataformat.linkcollector;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;

public class PathExtenderTest {
	@Test(expectedExceptions = InvocationTargetException.class)
	public void testHiddenConstructor() throws Exception {
		final Constructor<PathExtender> pathExtender = PathExtender.class.getDeclaredConstructor();
		pathExtender.setAccessible(true);
		pathExtender.newInstance();
	}

	@Test
	public void testExtendPathUsingDataGroup() {
		DataGroup dataGroup = createDataGroup();

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null, dataGroup);

		checkExtendedPath(extendedPath);
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("someData");
		return dataGroup;
	}

	private void checkExtendedPath(DataGroup extendedPath) {
		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"), "someData");
	}

	@Test
	public void testExtendPathUsingDataRecordLink() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"someData", "someRecordType", "someRecordId");

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null,
				dataRecordLink);

		checkExtendedPath(extendedPath);
	}

	@Test
	public void testExtendPathUsingDataGroupWithAttributes() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addAttributeByIdWithValue("attribute1", "attribute1Value");

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null, dataGroup);

		checkExtendedPath(extendedPath);
		DataGroup attributes = extendedPath.getFirstGroupWithNameInData("attributes");
		DataGroup attribute = attributes.getFirstGroupWithNameInData("attribute");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeName"), "attribute1");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeValue"),
				"attribute1Value");
	}

	@Test
	public void testExtendPathUsingDataGroupWithRepeatId() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.setRepeatId("e");

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null, dataGroup);

		checkExtendedPath(extendedPath);
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("repeatId"), "e");
	}

	@Test
	public void testExtendPathUsingDataGroupWithParentPath() {
		DataGroup dataGroup = createDataGroup();
		DataGroup parentPath = createPath();

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(parentPath,
				dataGroup);

		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"),
				"someNameInData");
		checkExtendedPath(extendedPath.getFirstGroupWithNameInData("linkedPath"));
	}

	private DataGroup createPath() {
		DataGroup pathToCopy = DataGroup.withNameInData("linkedPath");
		pathToCopy.addChild(DataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
		return pathToCopy;
	}

	@Test
	public void testExtendPathUsingDataGroupWithSuperParentPath() {
		DataGroup dataGroup = createDataGroup();
		DataGroup superParentPath = createPath();
		DataGroup parentPath = createPath();
		superParentPath.addChild(parentPath);

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(superParentPath,
				dataGroup);

		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"),
				"someNameInData");

		DataGroup middlePath = extendedPath.getFirstGroupWithNameInData("linkedPath");
		assertEquals(middlePath.getNameInData(), "linkedPath");
		assertEquals(middlePath.getFirstAtomicValueWithNameInData("nameInData"), "someNameInData");

		checkExtendedPath(extendedPath.getFirstGroupWithNameInData("linkedPath")
				.getFirstGroupWithNameInData("linkedPath"));
	}
}
