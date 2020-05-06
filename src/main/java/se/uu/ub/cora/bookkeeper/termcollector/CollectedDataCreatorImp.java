/*
 * Copyright 2017, 2019 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public final class CollectedDataCreatorImp implements CollectedDataCreator {

	@Override
	public DataGroup createCollectedDataFromCollectedTermsAndRecord(
			Map<String, List<DataGroup>> collectedTerms, DataGroup record) {
		DataGroup collectedData = createCollectedDataUsingIdentityFromRecord(record);
		addCollectedTermsToCollectedData(collectedTerms, collectedData);
		return collectedData;
	}

	private DataGroup createCollectedDataUsingIdentityFromRecord(DataGroup record) {
		String type = extractTypeFromRecord(record);
		String id = extractIdFromDataRecord(record);
		return createDataGroupWithTypeAndId(type, id);
	}

	private String extractTypeFromRecord(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup typeGroup = recordInfo.getFirstGroupWithNameInData("type");
		return typeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private String extractIdFromDataRecord(DataGroup collectTerm) {
		DataGroup recordInfo = collectTerm.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private DataGroup createDataGroupWithTypeAndId(String type, String id) {
		DataGroup collectedData = DataGroupProvider.getDataGroupUsingNameInData("collectedData");
		collectedData
				.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("type", type));
		collectedData.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("id", id));
		return collectedData;
	}

	private void addCollectedTermsToCollectedData(Map<String, List<DataGroup>> collectedTerms,
			DataGroup collectedData) {
		for (Entry<String, List<DataGroup>> entry : collectedTerms.entrySet()) {
			DataGroup termTypeGroup = DataGroupProvider.getDataGroupUsingNameInData(entry.getKey());
			addCollectedTermsToTermTypeGroup(termTypeGroup, entry.getValue());
			collectedData.addChild(termTypeGroup);
		}
	}

	private void addCollectedTermsToTermTypeGroup(DataGroup termTypeGroup, List<DataGroup> list) {
		int repeatId = 0;
		for (DataGroup collectedTerm : list) {
			collectedTerm.setRepeatId(String.valueOf(repeatId));
			termTypeGroup.addChild(collectedTerm);
			repeatId++;
		}
	}

	@Override
	public DataGroup createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(
			Map<String, List<DataGroup>> collectedTerms) {
		DataGroup collectedData = DataGroupProvider.getDataGroupUsingNameInData("collectedData");
		addCollectedTermsToCollectedData(collectedTerms, collectedData);
		return collectedData;
	}
}
