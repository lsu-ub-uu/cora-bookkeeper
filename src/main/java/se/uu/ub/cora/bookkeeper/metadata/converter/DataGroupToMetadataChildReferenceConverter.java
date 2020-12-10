/*
 * Copyright 2015, 2017, 2019, 2020 Uppsala University Library
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

import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.data.DataGroup;

public final class DataGroupToMetadataChildReferenceConverter {

	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private DataGroup dataGroup;
	private MetadataChildReference childReference;

	private DataGroupToMetadataChildReferenceConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToMetadataChildReferenceConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataChildReferenceConverter(dataGroup);
	}

	public MetadataChildReference toMetadata() {
		createMetadataChildReferenceWithBasicInfo();
		possiblyConvertCollectTerms();
		possiblyAddRecordPartConstraint();
		return childReference;
	}

	private void createMetadataChildReferenceWithBasicInfo() {
		int repeatMin = Integer.parseInt(dataGroup.getFirstAtomicValueWithNameInData("repeatMin"));
		int repeatMax = getRepeatMax();

		DataGroup ref = dataGroup.getFirstGroupWithNameInData("ref");
		String linkedRecordType = ref.getFirstAtomicValueWithNameInData("linkedRecordType");
		String linkedRecordId = ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		childReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(linkedRecordType,
						linkedRecordId, repeatMin, repeatMax);

	}

	private int getRepeatMax() {
		String repeatMaxString = dataGroup.getFirstAtomicValueWithNameInData("repeatMax");
		if ("X".equalsIgnoreCase(repeatMaxString)) {
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(repeatMaxString);
	}

	private void possiblyConvertCollectTerms() {
		if (dataGroup.containsChildWithNameInData("childRefCollectTerm")) {
			convertCollectTerms();
		}
	}

	private void convertCollectTerms() {
		List<DataGroup> childRefCollectIndexTerms = dataGroup
				.getAllGroupsWithNameInData("childRefCollectTerm");
		for (DataGroup collectIndexTermGroup : childRefCollectIndexTerms) {
			addCollectIndexTermToChildReference(collectIndexTermGroup);
		}
	}

	private void addCollectIndexTermToChildReference(DataGroup collectIndexTermGroup) {
		String collectTermId = collectIndexTermGroup
				.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		String type = collectIndexTermGroup.getAttribute("type").getValue();
		CollectTerm collectTerm = CollectTerm.createCollectTermWithTypeAndId(type, collectTermId);

		childReference.addCollectIndexTerm(collectTerm);
	}

	private void possiblyAddRecordPartConstraint() {
		if (childWithNameInDataExists("recordPartConstraint")) {
			String constraint = dataGroup.getFirstAtomicValueWithNameInData("recordPartConstraint");
			childReference.setRecordPartConstraint(ConstraintType.fromString(constraint));
		}
	}

	private boolean childWithNameInDataExists(String childNameInData) {
		return dataGroup.containsChildWithNameInData(childNameInData);
	}
}
