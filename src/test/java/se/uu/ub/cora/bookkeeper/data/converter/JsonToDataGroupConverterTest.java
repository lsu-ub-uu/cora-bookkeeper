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

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class JsonToDataGroupConverterTest {
	@Test
	public void testToClass() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[]}";
		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
	}

	private DataGroup createDataGroupForJsonString(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataGroupConverter
				.forJsonObject((JsonObject) jsonValue);
		DataPart dataPart = jsonToDataConverter.toInstance();
		DataGroup dataGroup = (DataGroup) dataPart;
		return dataGroup;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\"}";
		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		assertEquals(dataGroup.getRepeatId(), "3");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}, \"children\":[]}";
		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		String attributeValue = dataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}}";
		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		String attributeValue = dataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
		assertEquals(dataGroup.getRepeatId(), "3");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}"
				+ ",\"extraKey\":\"extra\"}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdMissingAttribute() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"NOTattributes\":{\"attributeNameInData\":\"attributeValue\"}}";
		createDataGroupForJsonString(json);
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{"
				+ "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},\"children\":[]}";

		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		String attributeValue = dataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = dataGroup.getAttributes().get("attributeNameInData2");
		assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		String json = "{\"name\":\"groupNameInData\","
				+ "\"children\":[{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}]}";

		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		DataAtomic child = (DataAtomic) dataGroup.getChildren().iterator().next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
	}

	@Test
	public void testToClassGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\","
				+ "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");
		Iterator<DataElement> iterator = dataGroup.getChildren().iterator();
		DataAtomic child = (DataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		DataGroup child2 = (DataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		DataAtomic subChild = (DataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");
	}

	@Test
	public void testToClassGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"attributes\":{" + "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\",";
		json += "\"attributes\":{\"g2AttributeNameInData\":\"g2AttributeValue\"},";
		json += "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		DataGroup dataGroup = createDataGroupForJsonString(json);
		assertEquals(dataGroup.getNameInData(), "groupNameInData");

		String attributeValue2 = dataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue2, "attributeValue");

		Iterator<DataElement> iterator = dataGroup.getChildren().iterator();
		DataAtomic child = (DataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		DataGroup child2 = (DataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		DataAtomic subChild = (DataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeNameInData");
		assertEquals(attributeValue, "g2AttributeValue");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoName() {
		String json = "{\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoChildren() {
		String json = "{\"name\":\"id\",\"attributes\":{}}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevel() {
		String json = "{\"name\":\"id\",\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevelWithAttributes() {
		String json = "{\"name\":\"id\",\"children\":[], \"attributes\":{},\"extra\":{\"id2\":\"value2\"}}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsGroup() {
		String json = "{\"name\":\"groupNameInData\", \"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":{} }}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTwoAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\"}"
				+ ",\"attributes\":{\"attributeNameInData2\":\"attributeValue2\"}}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":[true] }}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":[{\"attributeNameInData\":\"attributeValue\"}]}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},[]]}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsString() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},\"string\"]}";
		createDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonChildrenIsNotCorrectObject() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\""
				+ ",\"atomicNameInData2\":\"atomicValue2\"}]}";
		createDataGroupForJsonString(json);
	}
}
