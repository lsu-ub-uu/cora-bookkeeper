/*
 * Copyright 2015, 2018 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public final class DataGroupToMetadataConverterFactoryImp
		implements DataGroupToMetadataConverterFactory {
	private DataGroup dataGroup;

	private DataGroupToMetadataConverterFactoryImp(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToMetadataConverterFactoryImp fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataConverterFactoryImp(dataGroup);
	}

	@Override
	public DataGroupToMetadataConverter factor() {
		if ("metadata".equals(dataGroup.getNameInData())) {
			return createConverterBasedOnMetadataType();
		}
		throw DataConversionException.withMessage(
				"No converter found for DataGroup with nameInData:" + dataGroup.getNameInData());
	}

	private DataGroupToMetadataConverter createConverterBasedOnMetadataType() {
		String type = dataGroup.getAttributes().get("type");
		if ("group".equals(type)) {
			return DataGroupToMetadataGroupConverter.fromDataGroup(dataGroup);
		}
		if ("collectionItem".equals(type)) {
			return DataGroupToCollectionItemConverter.fromDataGroup(dataGroup);
		}
		if ("collectionVariable".equals(type)) {
			return DataGroupToCollectionVariableConverter.fromDataGroup(dataGroup);
		}
		if ("itemCollection".equals(type)) {
			return DataGroupToItemCollectionConverter.fromDataGroup(dataGroup);
		}
		if ("textVariable".equals(type)) {
			return DataGroupToTextVariableConverter.fromDataGroup(dataGroup);
		}
		if ("numberVariable".equals(type)) {
			return DataGroupToNumberVariableConverter.fromDataGroup(dataGroup);
		}
		if ("recordLink".equals(type)) {
			return DataGroupToRecordLinkConverter.fromDataGroup(dataGroup);
		}
		if ("resourceLink".equals(type)) {
			return DataGroupToResourceLinkConverter.fromDataGroup(dataGroup);
		}
		throw DataConversionException
				.withMessage("No converter found for DataGroup with type:" + type);
	}
}
