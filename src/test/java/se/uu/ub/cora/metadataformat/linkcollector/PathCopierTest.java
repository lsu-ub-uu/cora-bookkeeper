package se.uu.ub.cora.metadataformat.linkcollector;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;

public class PathCopierTest {

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testHiddenConstructor() throws Exception {
		final Constructor<PathCopier> pathCopier = PathCopier.class.getDeclaredConstructor();
		pathCopier.setAccessible(true);
		pathCopier.newInstance();
	}

	@Test
	public void testCopyPathNull() {
		DataGroup copiedPath = PathCopier.copyPath(null);
		Assert.assertNull(copiedPath);
	}

	@Test
	public void testCopyPath() {
		DataGroup pathToCopy = createPathToCopy();

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		checkCopiedPath(copiedPath);
	}

	private void checkCopiedPath(DataGroup copiedPath) {
		assertEquals(copiedPath.getNameInData(), "linkedPath");
		assertEquals(copiedPath.getFirstAtomicValueWithNameInData("nameInData"), "someNameInData");
	}

	private DataGroup createPathToCopy() {
		DataGroup pathToCopy = DataGroup.withNameInData("linkedPath");
		pathToCopy.addChild(DataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
		return pathToCopy;
	}

	@Test
	public void testCopyPathWithRepeatId() {
		DataGroup pathToCopy = createPathToCopy();
		pathToCopy.addChild(DataAtomic.withNameInDataAndValue("repeatId", "c"));

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		checkCopiedPath(copiedPath);
		assertEquals(copiedPath.getFirstAtomicValueWithNameInData("repeatId"), "c");
	}

	@Test
	public void testCopyPathWithAttributes() {
		DataGroup pathToCopy = createPathToCopy();
		pathToCopy.addChild(createAttributes());

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		checkCopiedPath(copiedPath);
		DataGroup attributes = copiedPath.getFirstGroupWithNameInData("attributes");
		DataGroup attribute = attributes.getFirstGroupWithNameInData("attribute");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeName"), "type");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeValue"), "person");
	}

	private DataGroup createAttributes() {
		DataGroup attributes = DataGroup.withNameInData("attributes");
		DataGroup attribute = DataGroup.withNameInData("attribute");
		attributes.addChild(attribute);
		attribute.addChild(DataAtomic.withNameInDataAndValue("attributeName", "type"));
		attribute.addChild(DataAtomic.withNameInDataAndValue("attributeValue", "person"));
		return attributes;
	}

	@Test
	public void testCopyPathWithNestedLinkedPath() {
		DataGroup pathToCopy = createPathToCopy();
		DataGroup pathToCopy2 = createPathToCopy();
		pathToCopy.addChild(pathToCopy2);

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		checkCopiedPath(copiedPath);
		DataGroup copiedSubPath = copiedPath.getFirstGroupWithNameInData("linkedPath");
		checkCopiedPath(copiedSubPath);
	}
}
