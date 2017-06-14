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

package se.uu.ub.cora.bookkeeper.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAttribute;
import se.uu.ub.cora.bookkeeper.data.DataPart;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class JsonToDataAttributeConverterTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"attributeNameInData\":\"attributeValue\"}";
		DataAttribute dataAttribute = createDataAttributeForJsonString(json);
		Assert.assertEquals(dataAttribute.getNameInData(), "attributeNameInData");
		Assert.assertEquals(dataAttribute.getValue(), "attributeValue");
	}

	private DataAttribute createDataAttributeForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter.forJsonObject((JsonObject) jsonValue);
		DataPart dataPart = jsonToDataConverter.toInstance();
		DataAttribute dataAttribute = (DataAttribute) dataPart;
		return dataAttribute;
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"attributeNameInData\":\"\"}";
		DataAttribute dataAttribute = createDataAttributeForJsonString(json);
		Assert.assertEquals(dataAttribute.getNameInData(), "attributeNameInData");
		Assert.assertEquals(dataAttribute.getValue(), "");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";

		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"attributeNameInData\":\"attributeValue\",\"id2\":\"value2\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"attributeNameInData\":\"attributeValue\",\"id2\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

}
