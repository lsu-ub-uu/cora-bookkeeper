/*
 * Copyright 2021, 2023 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

public class DataValidatorFactoryImp implements DataValidatorFactory {
	private MetadataStorageView metadataStorage = MetadataStorageProvider.getStorageView();
	private MetadataHolder metadataHolder = MetadataHolderProvider.getHolder();

	@Override
	public DataValidator factor() {
		Map<String, DataGroup> recordTypeHolder = createRecordTypeHolder(
				metadataStorage.getRecordTypes());

		DataElementValidatorFactory dataElementValidatorFactory = new DataElementValidatorFactoryImp(
				recordTypeHolder, metadataHolder);
		return new DataValidatorImp(dataElementValidatorFactory, recordTypeHolder);
	}

	private Map<String, DataGroup> createRecordTypeHolder(Collection<DataGroup> recordTypes) {
		Map<String, DataGroup> recordTypeHolder = new HashMap<>();
		for (DataGroup dataGroup : recordTypes) {
			addInfoForRecordTypeToHolder(recordTypeHolder, dataGroup);
		}
		return recordTypeHolder;
	}

	private void addInfoForRecordTypeToHolder(Map<String, DataGroup> recordTypeHolder,
			DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String recordId = recordInfo.getFirstAtomicValueWithNameInData("id");
		recordTypeHolder.put(recordId, dataGroup);
	}
}
