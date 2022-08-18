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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class PathCopierTest {

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
		final Constructor<PathCopier> pathCopier = PathCopier.class.getDeclaredConstructor();
		pathCopier.setAccessible(true);
		pathCopier.newInstance();
	}

	@Test
	public void testCopyPathNull() {
		DataGroup copiedPath = PathCopier.copyPath(null);
		Assert.assertNull(copiedPath);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 0);
	}

	@Test
	public void testCopyPath() {
		DataGroup pathToCopy = createPathToCopy();

		PathCopier.copyPath(pathToCopy);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 1);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someNameInData");
	}

	private DataGroup createPathToCopy() {
		DataGroup pathToCopy = new DataGroupOldSpy("linkedPath");
		pathToCopy.addChild(new DataAtomicSpy("nameInData", "someNameInData"));
		return pathToCopy;
	}

	private void assertCorrectAtomicDataUsingIndexNameInDataAndValue(int index, String nameInData,
			String value) {
		List<String> namesOfAtomicDataFactored = dataAtomicFactory.usedNameInDatas;
		List<String> valuesOfAtomicDataFactored = dataAtomicFactory.usedValues;
		assertEquals(namesOfAtomicDataFactored.get(index), nameInData);
		assertEquals(valuesOfAtomicDataFactored.get(index), value);

	}

	@Test
	public void testCopyPathWithRepeatId() {
		DataGroup pathToCopy = createPathToCopy();
		pathToCopy.addChild(new DataAtomicSpy("repeatId", "c"));

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someNameInData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "repeatId", "c");

		assertEquals(copiedPath.getFirstAtomicValueWithNameInData("repeatId"), "c");
	}

	@Test
	public void testCopyPathWithAttributes() {
		DataGroup pathToCopy = createPathToCopy();
		pathToCopy.addChild(createAttributes());

		DataGroup copiedPath = PathCopier.copyPath(pathToCopy);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 3);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "attributes");
		assertEquals(namesOfGroupsFactored.get(2), "attribute");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 3);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someNameInData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "attributeName", "type");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "attributeValue", "person");

		DataGroup attributes = copiedPath.getFirstGroupWithNameInData("attributes");
		DataGroup attribute = attributes.getFirstGroupWithNameInData("attribute");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeName"), "type");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeValue"), "person");
	}

	private DataGroup createAttributes() {
		DataGroup attributes = new DataGroupOldSpy("attributes");
		DataGroup attribute = new DataGroupOldSpy("attribute");
		attributes.addChild(attribute);
		attribute.addChild(new DataAtomicSpy("attributeName", "type"));
		attribute.addChild(new DataAtomicSpy("attributeValue", "person"));
		return attributes;
	}

	@Test
	public void testCopyPathWithNestedLinkedPath() {
		DataGroup pathToCopy = createPathToCopy();
		DataGroup pathToCopy2 = createPathToCopy();
		pathToCopy.addChild(pathToCopy2);

		PathCopier.copyPath(pathToCopy);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 2);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "linkedPath");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "someNameInData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "nameInData", "someNameInData");

	}
}
