/*
 * Copyright 2015, 2017, 2019, 2020 Uppsala University Library
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

import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.CollectTermLink;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToMetadataChildReferenceConverter {

	private DataGroup dataGroup;
	private MetadataChildReference childReference;

	private DataToMetadataChildReferenceConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataToMetadataChildReferenceConverter fromDataGroup(DataGroup dataGroup) {
		return new DataToMetadataChildReferenceConverter(dataGroup);
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

		DataRecordLink ref = dataGroup.getFirstChildOfTypeAndName(DataRecordLink.class, "ref");
		String linkedRecordType = ref.getLinkedRecordType();
		String linkedRecordId = ref.getLinkedRecordId();
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
		List<DataRecordLink> childRefCollectIndexTerms = dataGroup
				.getChildrenOfTypeAndName(DataRecordLink.class, "childRefCollectTerm");
		for (DataRecordLink collectIndexTermLink : childRefCollectIndexTerms) {
			addCollectIndexTermToChildReference(collectIndexTermLink);
		}
	}

	private void addCollectIndexTermToChildReference(DataRecordLink collectIndexTermLink) {
		String type = collectIndexTermLink.getAttribute("type").getValue();
		CollectTermLink collectTerm = CollectTermLink.createCollectTermWithTypeAndId(type,
				collectIndexTermLink.getLinkedRecordId());

		childReference.addCollectTerm(collectTerm);
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
