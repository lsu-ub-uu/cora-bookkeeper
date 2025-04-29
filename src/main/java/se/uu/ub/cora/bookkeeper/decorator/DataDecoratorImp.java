/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.decorator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;

public class DataDecoratorImp implements DataDecarator {

	private DataChildDecoratorFactory dataDecoratorFactory;

	public DataDecoratorImp(DataChildDecoratorFactory decoratorFactory) {
		this.dataDecoratorFactory = decoratorFactory;
	}

	@Override
	public void decorateDataGroup(String metadataId, DataGroup dataGroup) {
		decorateUsingDataGroup(metadataId, dataGroup);
	}

	private void decorateUsingDataGroup(String metadataId, DataGroup dataGroup) {
		try {
			tryToDecorateData(metadataId, dataGroup);
		} catch (Exception e) {
			throw DataDecaratorException.withMessageAndException(
					"Failed to decorate record using metadataid: " + metadataId, e);
		}
	}

	private void tryToDecorateData(String metadataId, DataGroup dataGroup) {
		var dataChildDecorator = dataDecoratorFactory.factor(metadataId);
		dataChildDecorator.decorateData(dataGroup);
	}

	public DataChildDecoratorFactory onlyForTestGetDataChildDecoratorFactory() {
		return dataDecoratorFactory;
	}

	@Override
	public void decorateRecord(String metadataId, DataRecord dataRecord) {
		DataGroup dataGroup = getDataGroupFromRecord(dataRecord);
		decorateUsingDataGroup(metadataId, dataGroup);
		setDataGroupToRecord(dataRecord, dataGroup);
	}

	private DataGroup getDataGroupFromRecord(DataRecord dataRecord) {
		DataRecordGroup dataRecordGroup = dataRecord.getDataRecordGroup();
		return DataProvider.createGroupFromRecordGroup(dataRecordGroup);
	}

	private void setDataGroupToRecord(DataRecord dataRecord, DataGroup dataGroup) {
		DataRecordGroup decoratedRecordGroup = DataProvider
				.createRecordGroupFromDataGroup(dataGroup);
		dataRecord.setDataRecordGroup(decoratedRecordGroup);
	}

}
