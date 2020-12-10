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

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		DataGroup redactedGroup = possiblyRemoveChildren(dataGroup, constraints, permissions,
				metadataGroup);

		return redactedGroup;
	}

	private DataGroup possiblyRemoveChildren(DataGroup dataGroup, Set<Constraint> constraints,
			Set<String> permissions, MetadataGroup metadataGroup) {
		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();
		DataGroup redactedGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroup, constraints, permissions);

		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			possiblyRemoveChild(constraints, permissions, redactedGroup, metadataChildReference);
		}
		return redactedGroup;
	}

	private void possiblyRemoveChild(Set<Constraint> constraints, Set<String> permissions,
			DataGroup redactedGroup, MetadataChildReference metadataChildReference) {
		if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)) {
			String childMetadataId = metadataChildReference.getLinkedRecordId();

			MetadataGroup childMetadataGroup = (MetadataGroup) metadataHolder
					.getMetadataElement(childMetadataId);
			String metadataNameInData = childMetadataGroup.getNameInData();

			if (redactedGroup.containsChildWithNameInData(metadataNameInData)) {
				DataGroup childDataGroup = redactedGroup
						.getFirstGroupWithNameInData(metadataNameInData);
				possiblyRemoveChildren(childDataGroup, constraints, permissions,
						childMetadataGroup);
			}
		}
	}

	private boolean repeatMaxIsOne(MetadataChildReference metadataChildReference) {
		return 1 == metadataChildReference.getRepeatMax();
	}

	private boolean isMetadataGroup(MetadataChildReference metadataChildReference) {
		return "metadataGroup".equals(metadataChildReference.getLinkedRecordType());
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
