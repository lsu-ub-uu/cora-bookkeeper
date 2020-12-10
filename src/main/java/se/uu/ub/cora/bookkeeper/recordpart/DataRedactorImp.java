/*
 * Copyright 2020 Uppsala University Library
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

import java.util.List;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorImp implements DataRedactor {

	private DataGroupRedactor dataGroupRedactor;
	private MetadataHolder metadataHolder;

	public DataRedactorImp(MetadataHolder metadataHolder, DataGroupRedactor dataGroupRedactor) {
		this.metadataHolder = metadataHolder;
		this.dataGroupRedactor = dataGroupRedactor;
	}

	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(String metadataId,
			DataGroup dataGroup, Set<Constraint> constraints, Set<String> permissions) {
		if (constraints.isEmpty()) {
			return dataGroup;
		}
		DataGroup temp = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroup,
				constraints, permissions);

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();

		// List<DataElement> dataChildren = temp.getChildren();
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			if ("metadataGroup".equals(metadataChildReference.getLinkedRecordType())
					&& 1 == metadataChildReference.getRepeatMax()) {
				String childMetadataId = metadataChildReference.getLinkedRecordId();
				MetadataGroup childGroup = (MetadataGroup) metadataHolder
						.getMetadataElement(childMetadataId);
				DataGroup firstGroupWithNameInData = temp
						.getFirstGroupWithNameInData(childGroup.getNameInData());
				removeChildrenForConstraintsWithoutPermissions(childMetadataId,
						firstGroupWithNameInData, constraints, permissions);
				// dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(firstGroupWithNameInData,
				// constraints, permissions);
			}

		}

		// läs topGroup //organisationGroup
		// läs upp barnen, loopa
		// är barnet en grupp och inte repeatble
		// hämta barnet i datat
		return temp;
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(String metadataId,
			DataGroup originalDataGroup, DataGroup updatedDataGroup, Set<Constraint> constraints,
			Set<String> permissions) {
		if (constraints.isEmpty()) {
			return originalDataGroup;
		}
		return dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, constraints, permissions);
	}

}
