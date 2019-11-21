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
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
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
		assertEquals(numberVariable.getId(), "someNumberVar");
		assertEquals(numberVariable.getNameInData(), "someNameInData");
		assertEquals(numberVariable.getTextId(), "someText");
		assertEquals(numberVariable.getDefTextId(), "someDefText");
	}

	@Test
	public void testnumberSpecificParameters() {
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
		DataGroup dataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someNumberVar"));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private DataGroup createTextGroupUsingNameInDataAndTextId(String nameInData, String textId) {
		DataGroup textIdGroup = new DataGroupSpy(nameInData);
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "coraText"));
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordId", textId));
		return textIdGroup;
	}

}
