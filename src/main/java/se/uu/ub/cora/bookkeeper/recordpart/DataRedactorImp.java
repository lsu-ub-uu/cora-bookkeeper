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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataAttribute;
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
		possiblyReplaceChild(originalDataGroup, updatedDataGroup, metadataGroup);
		return updatedDataGroup;
	}

	private void possiblyReplaceChild(DataGroup originalDataGroup, DataGroup updatedDataGroup,
			MetadataGroup metadataGroup) {
		DataGroupWrapperImp wrappedUpdated = wrapperFactory.factor(updatedDataGroup);
		DataGroup redactedDataGroup = dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup, wrappedUpdated,
						constraints, permissions);
		Map<String, List<List<DataAttribute>>> replacedChildren = wrappedUpdated
				.getRemovedNameInDatas();

		for (MetadataChildReference metadataChildReference : metadataGroup.getChildReferences()) {
			possiblyReplaceChild(originalDataGroup, redactedDataGroup, replacedChildren,
					metadataChildReference);
		}
	}

	private void possiblyReplaceChild(DataGroup originalDataGroup, DataGroup redactedDataGroup,
			Map<String, List<List<DataAttribute>>> replacedChildren,
			MetadataChildReference metadataChildReference) {
		if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)) {
			MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
					metadataChildReference);
			possiblyReplaceOrRemoveChildrenIfChildGroupHasData(originalDataGroup, redactedDataGroup,
					replacedChildren, childMetadataGroup);
		}
	}

	private void possiblyReplaceOrRemoveChildrenIfChildGroupHasData(DataGroup originalDataGroup,
			DataGroup redactedDataGroup, Map<String, List<List<DataAttribute>>> replacedChildren,
			MetadataGroup childMetadataGroup) {

		Matcher groupMatcher = matcherFactory.factor(redactedDataGroup, childMetadataGroup);
		if (groupMatcher.groupHasMatchingDataChild()) {
			DataElement updatedChild = groupMatcher.getMatchingDataChild();

			possiblyReplaceOrRemoveChild(originalDataGroup, redactedDataGroup, replacedChildren,
					childMetadataGroup, updatedChild);
		}
	}

	private void possiblyReplaceOrRemoveChild(DataGroup originalDataGroup,
			DataGroup redactedDataGroup, Map<String, List<List<DataAttribute>>> replacedChildren,
			MetadataGroup childMetadataGroup, DataElement updatedChild) {

		Matcher groupMatcher = matcherFactory.factor(originalDataGroup, childMetadataGroup);
		if (!groupMatcher.groupHasMatchingDataChild()) {
			dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(redactedDataGroup,
					constraints, permissions);
		} else {
			DataElement originalChild = groupMatcher.getMatchingDataChild();
			possiblyReplaceIfchildStillNeedsToBeCheckedForReplace(replacedChildren,
					childMetadataGroup, updatedChild, originalChild);
		}
	}

	private void possiblyReplaceIfchildStillNeedsToBeCheckedForReplace(
			Map<String, List<List<DataAttribute>>> replacedChildren,
			MetadataGroup childMetadataGroup, DataElement updatedChild, DataElement originalChild) {

		String metadataNameInData = childMetadataGroup.getNameInData();
		if (childStillNeedsToBeCheckedForReplace(replacedChildren, updatedChild,
				metadataNameInData)) {
			possiblyReplaceChild((DataGroup) originalChild, (DataGroup) updatedChild,
					childMetadataGroup);
		}
	}

	private boolean childStillNeedsToBeCheckedForReplace(
			Map<String, List<List<DataAttribute>>> replacedNameInDatas, DataElement updatedChild,
			String metadataNameInData) {
		// TODO: wanted method
		// dataGroupWrapper.isChildReplaced(updatedChild);
		return !childIsReplaced(replacedNameInDatas, updatedChild, metadataNameInData);
	}

	private boolean childIsReplaced(Map<String, List<List<DataAttribute>>> replacedNameInDatas,
			DataElement updatedChild, String metadataNameInData) {

		// updatedChild.getNameInData();
		// updatedChild.getAttributes()
		if (replacedNameInDatas.containsKey(metadataNameInData)) {
			return checkAttributesMatch(replacedNameInDatas, updatedChild, metadataNameInData);
		}
		return false;

	}

	private boolean checkAttributesMatch(Map<String, List<List<DataAttribute>>> replacedNameInDatas,
			DataElement updatedChild, String metadataNameInData) {
		List<List<DataAttribute>> alreadyReplacedChildAttributes = replacedNameInDatas
				.get(metadataNameInData);
		Collection<DataAttribute> updatedChildAttributes = updatedChild.getAttributes();
		return attributesExistsOnReplacedChildAttributes(updatedChildAttributes,
				alreadyReplacedChildAttributes);
	}

	private boolean attributesExistsOnReplacedChildAttributes(
			Collection<DataAttribute> updatedChildAttributes,
			List<List<DataAttribute>> alreadyReplacedChildAttributes) {
		for (List<DataAttribute> replacedAttributes : alreadyReplacedChildAttributes) {
			if (attributesInListMatch(updatedChildAttributes, replacedAttributes)) {
				return true;
			}
		}
		return false;
	}

	private boolean attributesInListMatch(Collection<DataAttribute> attributesInChildToCompare,
			List<DataAttribute> replacedAttributes) {
		if (haveDifferentSizes(attributesInChildToCompare, replacedAttributes)) {
			return false;
		}
		return findAnyMatchOnAttributes(attributesInChildToCompare, replacedAttributes);

	}

	private boolean haveDifferentSizes(Collection<DataAttribute> attributesInChildToCompare,
			List<DataAttribute> replacedAttributes) {
		return attributesInChildToCompare.size() != replacedAttributes.size();
	}

	private boolean findAnyMatchOnAttributes(Collection<DataAttribute> attributesInChildToCompare,
			List<DataAttribute> replacedAttributes) {
		for (DataAttribute childAttribute : attributesInChildToCompare) {
			if (matchNotFound(replacedAttributes, childAttribute)) {
				return false;
			}
		}
		return true;
	}

	private boolean matchNotFound(List<DataAttribute> replacedAttributes,
			DataAttribute childAttribute) {
		return replacedAttributes.stream().noneMatch(filterByNameAndValue(childAttribute));
	}

	private Predicate<DataAttribute> filterByNameAndValue(DataAttribute childAttribute) {
		return replacedAttribute -> dataAttributeIsSame(replacedAttribute, childAttribute);
	}

	private boolean dataAttributeIsSame(DataAttribute replacedAttribute,
			DataAttribute childAttribute) {
		return childAttribute.getNameInData().equals(replacedAttribute.getNameInData())
				&& childAttribute.getValue().equals(replacedAttribute.getValue());
	}

}
