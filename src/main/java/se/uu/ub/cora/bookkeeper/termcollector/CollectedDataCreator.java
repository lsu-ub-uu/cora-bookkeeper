/*
 * Copyright 2017 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public final class CollectedDataCreator {
	private List<DataGroup> collectedTerms;
	private DataGroup collectedData;

	public DataGroup createCollectedDataFromCollectedTermsAndDataGroup(List<DataGroup> collectedTerms, DataGroup dataGroup) {
		this.collectedTerms = collectedTerms;
		collectedData = DataGroup.withNameInData("collectedData");
		extractTypeFromDataGroupAndSetInCollectedData(dataGroup);
		extractIdFromDataGroupAndSetInCollectedData(dataGroup);
		addCollectedTermsToCollectedData();
		return collectedData;
	}

	private void addCollectedTermsToCollectedData() {
		if (!collectedTerms.isEmpty()) {
			int repeatId = 0;
			DataGroup index = DataGroup.withNameInData("index");
			collectedData.addChild(index);
			for (DataGroup collectedTerm : collectedTerms) {
				repeatId = addCollectedIndexTerm(repeatId, index, collectedTerm);
			}
		}
	}

	private int addCollectedIndexTerm(int repeatId, DataGroup index, DataGroup collectedTerm) {
		int newRepeatId = repeatId;
		if ("index".equals(collectedTerm.getAttribute("type"))) {
			collectedTerm.setRepeatId(String.valueOf(repeatId));
			index.addChild(collectedTerm);
			newRepeatId++;
		}
		return newRepeatId;
	}

	private void extractTypeFromDataGroupAndSetInCollectedData(DataGroup dataGroup) {
		String type = extractTypeFromDataGroup(dataGroup);
		collectedData.addChild(DataAtomic.withNameInDataAndValue("type", type));
	}

	private String extractTypeFromDataGroup(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup typeGroup = recordInfo.getFirstGroupWithNameInData("type");
		return typeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void extractIdFromDataGroupAndSetInCollectedData(DataGroup dataGroup) {
		String id = getCollectTermId(dataGroup);
		collectedData.addChild(DataAtomic.withNameInDataAndValue("id", id));
	}

	private String getCollectTermId(DataGroup collectTerm) {
		DataGroup recordInfo = collectTerm.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}
}
