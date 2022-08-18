/*
 * Copyright 2018, 2019 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToNumberVariableConverterTest {
	private DataGroup dataGroup;
	private DataGroupToNumberVariableConverter converter;
	private NumberVariable numberVariable;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroup();
		converter = DataGroupToNumberVariableConverter.fromDataGroup(dataGroup);
		numberVariable = (NumberVariable) converter.toMetadata();
	}

	@Test
	public void testStandardParameters() {
		assertCorrectStandardParameters();
	}

	private void assertCorrectStandardParameters() {
		assertEquals(numberVariable.getId(), "someNumberVar");
		assertEquals(numberVariable.getNameInData(), "someNameInData");
		assertEquals(numberVariable.getTextId(), "someText");
		assertEquals(numberVariable.getDefTextId(), "someDefText");
	}

	@Test
	public void testnumberSpecificParameters() {
		assertCorrectNumberSpecificParameters();
	}

	private void assertCorrectNumberSpecificParameters() {
		assertEquals(numberVariable.getMin(), 0.0);
		assertEquals(numberVariable.getMax(), 5.0);
		assertEquals(numberVariable.getWarningMin(), 1.0);
		assertEquals(numberVariable.getWarningMax(), 4.0);
		assertEquals(numberVariable.getNumOfDecmials(), 1);
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = createRecordInfo();
		dataGroup.addChild(new DataAtomicSpy("nameInData", "someNameInData"));
		dataGroup.addChild(createTextGroupUsingNameInDataAndTextId("textId", "someText"));
		dataGroup.addChild(createTextGroupUsingNameInDataAndTextId("defTextId", "someDefText"));

		dataGroup.addChild(new DataAtomicSpy("min", "0"));
		dataGroup.addChild(new DataAtomicSpy("max", "5"));
		dataGroup.addChild(new DataAtomicSpy("warningMin", "1"));
		dataGroup.addChild(new DataAtomicSpy("warningMax", "4"));
		dataGroup.addChild(new DataAtomicSpy("warningMax", "4"));
		dataGroup.addChild(new DataAtomicSpy("numberOfDecimals", "1"));
		return dataGroup;
	}

	private DataGroup createRecordInfo() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someNumberVar"));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private DataGroup createTextGroupUsingNameInDataAndTextId(String nameInData, String textId) {
		DataGroup textIdGroup = new DataGroupOldSpy(nameInData);
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "coraText"));
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordId", textId));
		return textIdGroup;
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataGroup dataGroup = createDataGroup();
		createAndAddAttributeReferences(dataGroup);

		DataGroupToNumberVariableConverter converter = DataGroupToNumberVariableConverter
				.fromDataGroup(dataGroup);
		MetadataElement numberVariable = converter.toMetadata();

		assertCorrectStandardParameters();
		assertCorrectNumberSpecificParameters();
		assertEquals(numberVariable.getAttributeReferences().size(), 2);
		assertEquals(numberVariable.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(numberVariable.getAttributeReferences().get(1), "someOtherCollectionVar");
	}

	private void createAndAddAttributeReferences(DataGroup dataGroup) {
		DataGroup attributeReferences = new DataGroupOldSpy("attributeReferences");
		attributeReferences.addChild(createRef("numberTypeCollectionVar", "0"));
		attributeReferences.addChild(createRef("someOtherCollectionVar", "1"));
		dataGroup.addChild(attributeReferences);
	}

	private DataGroupOldSpy createRef(String linkedRecordId, String repeatId) {
		DataGroupOldSpy ref = new DataGroupOldSpy("ref");
		ref.addChild(new DataAtomicSpy("linkedRecordType", "metadataCollectionVariable"));
		ref.addChild(new DataAtomicSpy("linkedRecordId", linkedRecordId));
		ref.setRepeatId(repeatId);
		return ref;
	}

}
