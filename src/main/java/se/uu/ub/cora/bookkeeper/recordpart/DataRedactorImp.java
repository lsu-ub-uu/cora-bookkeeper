/*
 * Copyright 2020 Uppsala University Library
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
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

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

	private void removeChildDataIfExists(DataGroup redactedDataGroup,
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
		DataGroupWrapper wrappedUpdated = wrapperFactory.factor(updatedDataGroup);
		possiblyReplaceChildren(originalDataGroup, wrappedUpdated, metadataGroup);
		return updatedDataGroup;
	}

	private void possiblyReplaceChildren(DataGroup originalDataGroup,
			DataGroupWrapper wrappedUpdated, MetadataGroup metadataGroup) {
		DataGroupWrapper wRedactedDataGroup = (DataGroupWrapper) dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup, wrappedUpdated,
						constraints, permissions);
		// TODO: wRedactedDataGroup now contains original data for children we do not have write
		// permission for
		for (MetadataChildReference metadataChildReference : metadataGroup.getChildReferences()) {
			// TODO: possibly check if child is removed(replaced) in wRedactedDataGroup before going
			// further
			possiblyReplaceChild(originalDataGroup, wRedactedDataGroup, metadataChildReference);
		}
	}

	private void possiblyReplaceChild(DataGroup originalDataGroup,
			DataGroupWrapper wRedactedDataGroup, MetadataChildReference metadataChildReference) {
		// TODO: if not done before check if child is removed(replaced) in wRedactedDataGroup before
		// going further
		if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)) {
			// TODO: check could be here if expensive...

			// dataForMetadataChildIsReplacedOrHasNoData

			// boolean dataForMetadataChildMustBeChecked = false;
			// MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
			// metadataChildReference);
			// Matcher groupMatcher = matcherFactory.factor(wRedactedDataGroup, childMetadataGroup);
			// if (groupMatcher.groupHasMatchingDataChild()) {
			// DataGroup updatedChild = groupMatcher.getMatchingDataChild();
			// dataForMetadataChildMustBeChecked = !wRedactedDataGroup
			// .hasRemovedBeenCalled(updatedChild);
			// }

			// if (dataForMetadataChildMustBeChecked) {
			MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
					metadataChildReference);
			possiblyReplaceOrRemoveChildrenIfChildGroupHasData(originalDataGroup,
					wRedactedDataGroup, childMetadataGroup);
			// }
		}
	}

	private void possiblyReplaceOrRemoveChildrenIfChildGroupHasData(DataGroup originalDataGroup,
			DataGroupWrapper wRedactedDataGroup, MetadataGroup childMetadataGroup) {

		Matcher groupMatcher = matcherFactory.factor(wRedactedDataGroup, childMetadataGroup);
		if (groupMatcher.groupHasMatchingDataChild()) {
			DataGroup updatedChild = groupMatcher.getMatchingDataChild();
			// TODO: wrappedChild here is for level two and lower, top level child forgot about :(
			DataGroupWrapper wrappedChild = wrapperFactory.factor(updatedChild);

			possiblyReplaceOrRemoveChild(originalDataGroup, childMetadataGroup, updatedChild,
					wrappedChild);
		}
	}

	private void possiblyReplaceOrRemoveChild(DataGroup originalDataGroup,
			MetadataGroup childMetadataGroup, DataGroup updatedChild,
			DataGroupWrapper wrappedUpdated) {

		Matcher groupMatcher = matcherFactory.factor(originalDataGroup, childMetadataGroup);
		if (!groupMatcher.groupHasMatchingDataChild()) {
			dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(updatedChild,
					constraints, permissions);
		} else {
			DataElement originalChild = groupMatcher.getMatchingDataChild();
			possiblyReplaceIfchildStillNeedsToBeCheckedForReplace(childMetadataGroup, updatedChild,
					originalChild, wrappedUpdated);
		}
	}

	private void possiblyReplaceIfchildStillNeedsToBeCheckedForReplace(
			MetadataGroup childMetadataGroup, DataElement updatedChild, DataElement originalChild,
			DataGroupWrapper wrappedUpdated) {

		if (childStillNeedsToBeCheckedForReplace(updatedChild, wrappedUpdated)) {
			possiblyReplaceChildren((DataGroup) originalChild, wrappedUpdated, childMetadataGroup);
		}
	}

	private boolean childStillNeedsToBeCheckedForReplace(DataElement updatedChild,
			DataGroupWrapper wrappedUpdated) {
		return !wrappedUpdated.hasRemovedBeenCalled(updatedChild);
	}

	public MetadataHolder getMetadataHolder() {
		return metadataHolder;
	}

	public DataGroupRedactor getDataGroupRedactor() {
		return dataGroupRedactor;
	}

	public DataGroupWrapperFactory getDataGroupWrapperFactory() {
		return wrapperFactory;
	}

	public MatcherFactory getMatcherFactory() {
		return matcherFactory;
	}

}
