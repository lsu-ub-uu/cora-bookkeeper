/*
 * Copyright 2020, 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MetadataGroupSpy extends MetadataGroup {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	List<MetadataChildReference> childReferencesToReturn = new ArrayList<>();

	public MetadataGroupSpy() {
		super("someId", "someNameInData", null, null);
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getChildReferences", () -> childReferencesToReturn);
	}

	public MetadataGroupSpy(String id, String nameInData) {
		super(id, nameInData, "", "");
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getChildReferences", () -> childReferencesToReturn);
	}

	public MetadataGroupSpy(String id, String nameInData, String textId, String defTextId) {
		super(id, nameInData, textId, defTextId);
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getChildReferences", () -> childReferencesToReturn);
	}

	public void createChildReference(String linkedRecordType, String linkedRecordId, int repeatMin,
			int repeatMax) {
		MetadataChildReference childReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(linkedRecordType,
						linkedRecordId, repeatMin, repeatMax);
		childReferencesToReturn.add(childReference);
	}

	public void createChildReference(String linkedRecordType, String linkedRecordId, int repeatMin,
			int repeatMax, ConstraintType constraint) {
		MetadataChildReference childReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(linkedRecordType,
						linkedRecordId, repeatMin, repeatMax);
		childReference.setRecordPartConstraint(constraint);
		childReferencesToReturn.add(childReference);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MetadataChildReference> getChildReferences() {
		// return childReferencesToReturn;
		return (List<MetadataChildReference>) MCR.addCallAndReturnFromMRV();
	}

}
