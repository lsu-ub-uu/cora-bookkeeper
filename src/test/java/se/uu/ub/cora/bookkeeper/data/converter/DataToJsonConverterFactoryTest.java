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

package se.uu.ub.cora.bookkeeper.data.converter;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataToJsonConverterFactoryTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
	}

	@Test
	public void testJsonCreatorFactoryDataGroup() {
		DataPart dataPart = DataGroup.withNameInData("groupNameInData");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataPart);

		assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		DataPart dataPart = DataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataPart);

		assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAttribute() {
		DataAttribute dataAttribute = DataAttribute.withNameInDataAndValue("attributeNameInData",
				"attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataAttribute);

		assertTrue(dataToJsonConverter instanceof DataAttributeToJsonConverter);
	}

}
