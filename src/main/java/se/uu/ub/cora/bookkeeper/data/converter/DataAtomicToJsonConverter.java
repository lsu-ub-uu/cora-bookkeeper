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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public final class DataAtomicToJsonConverter extends DataToJsonConverter {

	private DataAtomic dataAtomic;
	private JsonBuilderFactory factory;

	public static DataToJsonConverter usingJsonFactoryForDataAtomic(JsonBuilderFactory factory,
			DataAtomic dataAtomic) {
		return new DataAtomicToJsonConverter(factory, dataAtomic);
	}

	private DataAtomicToJsonConverter(JsonBuilderFactory factory, DataAtomic dataAtomic) {
		this.factory = factory;
		this.dataAtomic = dataAtomic;
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString("name", dataAtomic.getNameInData());
		jsonObjectBuilder.addKeyString("value", dataAtomic.getValue());
		possiblyAddRepeatId(jsonObjectBuilder);
		return jsonObjectBuilder;
	}

	private void possiblyAddRepeatId(JsonObjectBuilder jsonObjectBuilder) {
		if (hasNonEmptyRepeatId()) {
			jsonObjectBuilder.addKeyString("repeatId", dataAtomic.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return dataAtomic.getRepeatId() != null && !dataAtomic.getRepeatId().equals("");
	}
}
