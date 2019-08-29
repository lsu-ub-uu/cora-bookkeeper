/*
 * Copyright 2015, 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;

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
