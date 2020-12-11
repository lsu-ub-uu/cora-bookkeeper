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
	private Set<Constraint> constraints;
	private Set<String> permissions;

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
		this.constraints = constraints;
		this.permissions = permissions;

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		return possiblyRemoveChildren(dataGroup, metadataGroup);
	}

	private DataGroup possiblyRemoveChildren(DataGroup dataGroup, MetadataGroup metadataGroup) {
		DataGroup redactedGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroup, constraints, permissions);

		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			possiblyRemoveChild(redactedGroup, metadataChildReference);
		}
		return redactedGroup;
	}

	private void possiblyRemoveChild(DataGroup redactedGroup,
			MetadataChildReference metadataChildReference) {
		if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)) {
			removeChildDataIfExists(redactedGroup, metadataChildReference);
		}
	}

	private void removeChildDataIfExists(DataGroup redactedGroup,
			MetadataChildReference metadataChildReference) {

		MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
				metadataChildReference);
		String metadataNameInData = childMetadataGroup.getNameInData();

		if (dataExistsForMetadata(redactedGroup, metadataNameInData)) {
			removeChildData(redactedGroup, childMetadataGroup, metadataNameInData);
		}
	}

	private void removeChildData(DataGroup redactedGroup, MetadataGroup childMetadataGroup,
			String metadataNameInData) {
		DataGroup childDataGroup = redactedGroup.getFirstGroupWithNameInData(metadataNameInData);
		possiblyRemoveChildren(childDataGroup, childMetadataGroup);
	}

	private boolean dataExistsForMetadata(DataGroup redactedGroup, String metadataNameInData) {
		return redactedGroup.containsChildWithNameInData(metadataNameInData);
	}

	private boolean repeatMaxIsOne(MetadataChildReference metadataChildReference) {
		return metadataChildReference.getRepeatMax() == 1;
	}

	private boolean isMetadataGroup(MetadataChildReference metadataChildReference) {
		return "metadataGroup".equals(metadataChildReference.getLinkedRecordType());
	}

	private MetadataGroup getMetadataChildFromMetadataHolder(
			MetadataChildReference metadataChildReference) {
		String childMetadataId = metadataChildReference.getLinkedRecordId();

		return (MetadataGroup) metadataHolder.getMetadataElement(childMetadataId);
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(String metadataId,
			DataGroup originalDataGroup, DataGroup updatedDataGroup, Set<Constraint> constraints,
			Set<String> permissions) {
		if (constraints.isEmpty()) {
			return originalDataGroup;
		}

		this.constraints = constraints;
		this.permissions = permissions;

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);

		// metadataGroup.getChildReferences()
		DataGroupWrapper wrappedUpdated = new DataGroupWrapper(updatedDataGroup);
		// return
		// dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
		// updatedDataGroup, constraints, permissions);
		return dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				wrappedUpdated, constraints, permissions);
		List<String> removedIds = wrappedUpdated.getListOfRemovedIds();

		// loop children
		if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)
				&& notInRemovedIdsList()) {
			replaceChildDataIfExists(updatedDataGroup, metadataChildReference);
		}

	}

}
