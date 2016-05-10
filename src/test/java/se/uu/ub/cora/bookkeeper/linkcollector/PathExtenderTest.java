/*
 * Copyright 2015 Uppsala University Library
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
import static org.testng.Assert.assertFalse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

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
		return DataGroup.withNameInData("someData");
	}

	private void checkExtendedPath(DataGroup extendedPath) {
		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"), "someData");
	}

	@Test
	public void testExtendPathUsingDataRecordLink() {
		DataGroup dataRecordLink = DataGroup.withNameInData("someData");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"someRecordType");
		dataRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"someRecordId");
		dataRecordLink.addChild(linkedRecordId);

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

	@Test
	public void testExtendPathWithAtomicElement() {
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"someRecordType");
		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null,
				linkedRecordType);
		assertFalse(extendedPath.containsChildWithNameInData("attributes"));
	}
}
