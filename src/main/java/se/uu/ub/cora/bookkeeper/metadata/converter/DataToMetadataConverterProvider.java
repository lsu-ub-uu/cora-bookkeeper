/*
 * Copyright 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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

import se.uu.ub.cora.data.DataRecordGroup;

public class DataToMetadataConverterProvider {

	private static DataToMetadataConverterFactory dataGroupToMetadataConverterFactory;

	private DataToMetadataConverterProvider() {
		// not called
		throw new UnsupportedOperationException();
	}

	public static DataToMetadataConverter getConverter(DataRecordGroup dataRecordGroup) {
		if (dataGroupToMetadataConverterFactory == null) {
			dataGroupToMetadataConverterFactory = DataToMetadataConverterFactoryImp.forDataGroups();
		}
		return dataGroupToMetadataConverterFactory.factorForDataContainingMetadata(dataRecordGroup);
	}

	/**
	 * onlyForTestSetDataGroupToMetadataConverterFactory sets a DataGroupToMetadataConverterFactory
	 * that will be used to return instances for the {@link #DataGroupToMetadataConverter()} method.
	 * This possibility to set a DataGroupToMetadataConverterFactory is provided to enable testing
	 * of getting a converter in other classes and is not intented to be used in production.
	 * <p>
	 * 
	 * @param dataGroupToMetadataConverterFactoryIn
	 *            A DataGroupToMetadataConverterFactory to use to return
	 *            DataGroupToMetadataConverter instances for testing
	 */
	public static void onlyForTestSetDataGroupToMetadataConverterFactory(
			DataToMetadataConverterFactory dataGroupToMetadataConverterFactoryIn) {
		dataGroupToMetadataConverterFactory = dataGroupToMetadataConverterFactoryIn;
	}

	static DataToMetadataConverterFactory onlyForTestGetDataGroupToMetadataConverterFactory() {
		return dataGroupToMetadataConverterFactory;
	}

}
