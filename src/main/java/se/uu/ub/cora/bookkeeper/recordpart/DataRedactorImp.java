/*
 * Copyright 2020, 2024 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Publiåc License as published by
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
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;

public class DataRedactorImp implements DataRedactor {

	private DataGroupRedactor dataGroupRedactor;
	private MetadataHolder metadataHolder;
	private Set<Constraint> constraints;
	private Set<String> permissions;
	private DataGroupWrapperFactory wrapperFactory;
	private MatcherFactory matcherFactory;

	public DataRedactorImp(MetadataHolder metadataHolder, DataGroupRedactor dataGroupRedactor,
			DataGroupWrapperFactory wrapperFactory, MatcherFactory matcherFactory) {
		this.metadataHolder = metadataHolder;
		this.dataGroupRedactor = dataGroupRedactor;
		this.wrapperFactory = wrapperFactory;
		this.matcherFactory = matcherFactory;
	}

	@Override
	public DataRecordGroup removeChildrenForConstraintsWithoutPermissions(String metadataId,
			DataRecordGroup dataRecordGroup, Set<Constraint> constraints, Set<String> permissions) {
		if (constraints.isEmpty()) {
			return dataRecordGroup;
		}
		this.constraints = constraints;
		this.permissions = permissions;

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		DataGroup dataGroup = DataProvider.createGroupFromRecordGroup(dataRecordGroup);
		DataGroup redactedGroup = possiblyRemoveChildren(dataGroup, metadataGroup);
		return DataProvider.createRecordGroupFromDataGroup(redactedGroup);
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
		if (childReferenceRepresentsGroupWithRepeatMaxOne(metadataChildReference)) {
			possiblyRemoveChildDataIfExists(redactedGroup, metadataChildReference);
		}
	}

	private void possiblyRemoveChildDataIfExists(DataGroup redactedDataGroup,
			MetadataChildReference metadataChildReference) {
		MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
				metadataChildReference);

		Matcher groupMatcher = matcherFactory.factor(redactedDataGroup, childMetadataGroup);
		if (groupMatcher.groupHasMatchingDataChild()) {
			DataGroup childDataGroup = groupMatcher.getMatchingDataChild();
			possiblyRemoveChildren(childDataGroup, childMetadataGroup);
		}
	}

	private boolean repeatMaxIsOne(MetadataChildReference metadataChildReference) {
		return metadataChildReference.getRepeatMax() == 1;
	}

	private MetadataGroup getMetadataChildFromMetadataHolder(
			MetadataChildReference metadataChildReference) {
		String childMetadataId = metadataChildReference.getLinkedRecordId();

		return (MetadataGroup) metadataHolder.getMetadataElement(childMetadataId);
	}

	@Override
	public DataRecordGroup replaceChildrenForConstraintsWithoutPermissions(String metadataId,
			DataRecordGroup originalDataRecordGroup, DataRecordGroup updatedDataRecordGroup,
			Set<Constraint> constraints, Set<String> permissions) {
		if (constraints.isEmpty()) {
			return originalDataRecordGroup;
		}

		this.constraints = constraints;
		this.permissions = permissions;

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		DataGroup originalAsDataGroup = DataProvider
				.createGroupFromRecordGroup(originalDataRecordGroup);
		DataGroup updatedAsDataGroup = DataProvider
				.createGroupFromRecordGroup(updatedDataRecordGroup);
		possiblyReplaceChildren(originalAsDataGroup, updatedAsDataGroup, metadataGroup);
		return DataProvider.createRecordGroupFromDataGroup(updatedAsDataGroup);
	}

	private void possiblyReplaceChildren(DataGroup originalDataGroup, DataGroup updatedDataGroup,
			MetadataGroup metadataGroup) {
		DataGroupWrapper wReplacedDataGroup = wrappAndReplaceDataGroup(originalDataGroup,
				updatedDataGroup);
		for (MetadataChildReference metadataChildReference : metadataGroup.getChildReferences()) {
			possiblyReplaceChild(originalDataGroup, wReplacedDataGroup, metadataChildReference);
		}
	}

	private DataGroupWrapper wrappAndReplaceDataGroup(DataGroup originalDataGroup,
			DataGroup updatedDataGroup) {
		DataGroupWrapper wrappedUpdated = wrapperFactory.factor(updatedDataGroup);
		return (DataGroupWrapper) dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(
				originalDataGroup, wrappedUpdated, constraints, permissions);
	}

	private void possiblyReplaceChild(DataGroup originalDataGroup,
			DataGroupWrapper wUpdatedDataGroup, MetadataChildReference metadataChildReference) {
		if (childReferenceRepresentsGroupWithRepeatMaxOne(metadataChildReference)) {
			possiblyReplaceGroupChildWithRepeatMaxOne(originalDataGroup, wUpdatedDataGroup,
					metadataChildReference);
		}
	}

	private boolean childReferenceRepresentsGroupWithRepeatMaxOne(
			MetadataChildReference metadataChildReference) {
		return isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference);
	}

	private boolean isMetadataGroup(MetadataChildReference metadataChildReference) {
		String childMetadataId = metadataChildReference.getLinkedRecordId();
		MetadataElement metadataElement = metadataHolder.getMetadataElement(childMetadataId);
		return metadataElement instanceof MetadataGroup;
	}

	private void possiblyReplaceGroupChildWithRepeatMaxOne(DataGroup originalDataGroup,
			DataGroupWrapper wUpdatedDataGroup, MetadataChildReference metadataChildReference) {
		MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
				metadataChildReference);
		boolean childHasDataAndItIsUnchanged = false;
		Matcher updatedGroupMatcher = matcherFactory.factor(wUpdatedDataGroup, childMetadataGroup);
		if (updatedGroupMatcher.groupHasMatchingDataChild()) {
			DataGroup updatedChild = updatedGroupMatcher.getMatchingDataChild();
			childHasDataAndItIsUnchanged = !wUpdatedDataGroup.hasRemovedBeenCalled(updatedChild);
			if (childHasDataAndItIsUnchanged) {
				possiblyReplaceOrRemoveChildForChildGroupWithData(originalDataGroup, updatedChild,
						childMetadataGroup);
			}
		}
	}

	private void possiblyReplaceOrRemoveChildForChildGroupWithData(DataGroup originalDataGroup,
			DataGroup updatedChild, MetadataGroup childMetadataGroup) {
		Matcher originalGroupMatcher = matcherFactory.factor(originalDataGroup, childMetadataGroup);
		if (originalGroupMatcher.groupHasMatchingDataChild()) {
			DataGroup originalChild = originalGroupMatcher.getMatchingDataChild();
			possiblyReplaceChildren(originalChild, updatedChild, childMetadataGroup);
		} else {
			possiblyRemoveChildren(updatedChild, childMetadataGroup);
		}
	}

	public MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

	public DataGroupRedactor onlyForTestGetDataGroupRedactor() {
		return dataGroupRedactor;
	}

	public DataGroupWrapperFactory onlyForTestGetDataGroupWrapperFactory() {
		return wrapperFactory;
	}

	public MatcherFactory onlyForTestGetMatcherFactory() {
		return matcherFactory;
	}

}
