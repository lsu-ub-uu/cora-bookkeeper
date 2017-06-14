/*
 * Copyright 2015 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;
	private DataGroup dataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
		dataGroup = DataGroup.withNameInData("groupNameInData");
	}

	@Test
	public void testToJson() {
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\"name\": \"groupNameInData\"}";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonWithRepeatId() {
		dataGroup.setRepeatId("4");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{\n";
		expectedJson += "    \"repeatId\": \"4\",\n";
		expectedJson += "    \"name\": \"groupNameInData\"\n";
		expectedJson += "}";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonWithEmptyRepeatId() {
		dataGroup.setRepeatId("");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{\"name\": \"groupNameInData\"}";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		dataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\n";
		expectedJson += "    \"name\": \"groupNameInData\",\n";
		expectedJson += "    \"attributes\": {\"attributeNameInData\": \"attributeValue\"}\n";
		expectedJson += "}";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		dataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\n";
		expectedJson += "    \"name\": \"groupNameInData\",\n";
		expectedJson += "    \"attributes\": {\n";
		expectedJson += "        \"attributeNameInData2\": \"attributeValue2\",\n";
		expectedJson += "        \"attributeNameInData\": \"attributeValue\"\n";
		expectedJson += "    }\n";
		expectedJson += "}";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\n";
		expectedJson += "    \"children\": [{\n";
		expectedJson += "        \"name\": \"atomicNameInData\",\n";
		expectedJson += "        \"value\": \"atomicValue\"\n";
		expectedJson += "    }],\n";
		expectedJson += "    \"name\": \"groupNameInData\"\n";
		expectedJson += "}";
		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		DataGroup dataGroup2 = DataGroup.withNameInData("groupNameInData2");
		dataGroup.addChild(dataGroup2);

		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{\n";
		expectedJson += "    \"children\": [\n";
		expectedJson += "        {\n";
		expectedJson += "            \"name\": \"atomicNameInData\",\n";
		expectedJson += "            \"value\": \"atomicValue\"\n";
		expectedJson += "        },\n";
		expectedJson += "        {\n";
		expectedJson += "            \"children\": [{\n";
		expectedJson += "                \"name\": \"atomicNameInData2\",\n";
		expectedJson += "                \"value\": \"atomicValue2\"\n";
		expectedJson += "            }],\n";
		expectedJson += "            \"name\": \"groupNameInData2\"\n";
		expectedJson += "        }\n";
		expectedJson += "    ],\n";
		expectedJson += "    \"name\": \"groupNameInData\"\n";
		expectedJson += "}";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		dataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("createdBy", "userId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		DataGroup dataGroup2 = DataGroup.withNameInData("groupNameInData2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeNameInData", "g2AttributeValue");
		dataGroup.addChild(dataGroup2);

		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForDataElement(factory, dataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\n";
		expectedJson += "    \"children\": [\n";
		expectedJson += "        {\n";
		expectedJson += "            \"children\": [\n";
		expectedJson += "                {\n";
		expectedJson += "                    \"name\": \"id\",\n";
		expectedJson += "                    \"value\": \"place:0001\"\n";
		expectedJson += "                },\n";
		expectedJson += "                {\n";
		expectedJson += "                    \"name\": \"type\",\n";
		expectedJson += "                    \"value\": \"place\"\n";
		expectedJson += "                },\n";
		expectedJson += "                {\n";
		expectedJson += "                    \"name\": \"createdBy\",\n";
		expectedJson += "                    \"value\": \"userId\"\n";
		expectedJson += "                }\n";
		expectedJson += "            ],\n";
		expectedJson += "            \"name\": \"recordInfo\"\n";
		expectedJson += "        },\n";
		expectedJson += "        {\n";
		expectedJson += "            \"name\": \"atomicNameInData\",\n";
		expectedJson += "            \"value\": \"atomicValue\"\n";
		expectedJson += "        },\n";
		expectedJson += "        {\n";
		expectedJson += "            \"children\": [{\n";
		expectedJson += "                \"name\": \"atomicNameInData2\",\n";
		expectedJson += "                \"value\": \"atomicValue2\"\n";
		expectedJson += "            }],\n";
		expectedJson += "            \"name\": \"groupNameInData2\",\n";
		expectedJson += "            \"attributes\": {\"g2AttributeNameInData\": \"g2AttributeValue\"}\n";
		expectedJson += "        }\n";
		expectedJson += "    ],\n";
		expectedJson += "    \"name\": \"groupNameInData\",\n";
		expectedJson += "    \"attributes\": {\n";
		expectedJson += "        \"attributeNameInData2\": \"attributeValue2\",\n";
		expectedJson += "        \"attributeNameInData\": \"attributeValue\"\n";
		expectedJson += "    }\n";
		expectedJson += "}";
		assertEquals(json, expectedJson);
	}

}
