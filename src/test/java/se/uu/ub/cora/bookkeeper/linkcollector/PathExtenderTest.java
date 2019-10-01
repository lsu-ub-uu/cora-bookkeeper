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
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class PathExtenderTest {

	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

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
		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");

		checkExtendedPath(extendedPath);
	}

	private void assertCorrectAtomicDataUsingIndexNameInDataAndValue(int index, String nameInData,
			String value) {
		List<String> namesOfAtomicDataFactored = dataAtomicFactory.usedNameInDatas;
		List<String> valuesOfAtomicDataFactored = dataAtomicFactory.usedValues;
		assertEquals(namesOfAtomicDataFactored.get(index), nameInData);
		assertEquals(valuesOfAtomicDataFactored.get(index), value);

	}

	private DataGroup createDataGroup() {
		return new DataGroupSpy("someData");
	}

	private void checkExtendedPath(DataGroup extendedPath) {
		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"), "someData");
	}

	@Test
	public void testExtendPathUsingDataRecordLink() {
		DataGroup dataRecordLink = new DataGroupSpy("someData");

		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordId);

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(null,
				dataRecordLink);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");

		checkExtendedPath(extendedPath);
	}

	@Test
	public void testExtendPathUsingDataGroupWithAttributes() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addAttributeByIdWithValue("attribute1", "attribute1Value");

		PathExtender.extendPathWithElementInformation(null, dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 3);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "attributes");
		assertEquals(namesOfGroupsFactored.get(2), "attribute");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 3);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "attributeName", "attribute1");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "attributeValue", "attribute1Value");
	}

	@Test
	public void testExtendPathUsingDataGroupWithRepeatId() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.setRepeatId("e");

		PathExtender.extendPathWithElementInformation(null, dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "repeatId", "e");

	}

	@Test
	public void testExtendPathUsingDataGroupWithParentPath() {
		DataGroup dataGroup = createDataGroup();
		DataGroup parentPath = createPath();

		DataGroup extendedPath = PathExtender.extendPathWithElementInformation(parentPath,
				dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");

		assertEquals(extendedPath.getNameInData(), "linkedPath");
		assertEquals(extendedPath.getFirstAtomicValueWithNameInData("nameInData"),
				"someNameInData");
		checkExtendedPath(extendedPath.getFirstGroupWithNameInData("linkedPath"));
	}

	private DataGroup createPath() {
		DataGroup pathToCopy = new DataGroupSpy("linkedPath");
		pathToCopy.addChild(new DataAtomicSpy("nameInData", "someNameInData"));
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

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someData");
	}

	@Test
	public void testExtendPathWithAtomicElement() {
		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "someRecordType");

		PathExtender.extendPathWithElementInformation(null, linkedRecordType);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "linkedRecordType");
	}
}
