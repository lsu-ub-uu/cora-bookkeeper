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

import java.util.Map.Entry;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataPart;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private DataGroup dataGroup;
	private JsonObjectBuilder dataGroupJsonObjectBuilder;
	private JsonBuilderFactory jsonBuilderFactory;

	public static DataGroupToJsonConverter usingJsonFactoryForDataGroup(JsonBuilderFactory factory,
			DataGroup dataGroup) {
		return new DataGroupToJsonConverter(factory, dataGroup);
	}

	private DataGroupToJsonConverter(JsonBuilderFactory factory, DataGroup dataGroup) {
		this.jsonBuilderFactory = factory;
		this.dataGroup = dataGroup;
		dataGroupJsonObjectBuilder = factory.createObjectBuilder();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddRepeatId();
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		dataGroupJsonObjectBuilder.addKeyString("name", dataGroup.getNameInData());
		return dataGroupJsonObjectBuilder;
	}

	private void possiblyAddRepeatId() {
		if (hasNonEmptyRepeatId()) {
			dataGroupJsonObjectBuilder.addKeyString("repeatId", dataGroup.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return dataGroup.getRepeatId() != null && !dataGroup.getRepeatId().equals("");
	}

	private boolean hasAttributes() {
		return !dataGroup.getAttributes().isEmpty();
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : dataGroup.getAttributes().entrySet()) {
			attributes.addKeyString(attributeEntry.getKey(), attributeEntry.getValue());
		}
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private boolean hasChildren() {
		return !dataGroup.getChildren().isEmpty();
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (DataElement dataElement : dataGroup.getChildren()) {
			DataPart dataPart = (DataPart) dataElement;
			childrenArray.addJsonObjectBuilder(dataToJsonConverterFactory
					.createForDataElement(jsonBuilderFactory, dataPart).toJsonObjectBuilder());
		}
		dataGroupJsonObjectBuilder.addKeyJsonArrayBuilder("children", childrenArray);
	}
}
