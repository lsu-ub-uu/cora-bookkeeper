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
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataFactory;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorImp implements DataRedactor {

	private DataGroupRedactor dataGroupRedactor;
	private MetadataHolder metadataHolder;
	private Set<Constraint> constraints;
	private Set<String> permissions;
	private MetadataMatchDataFactory matchFactory;
	private DataGroupWrapperFactory wrapperFactory;

	public DataRedactorImp(MetadataHolder metadataHolder, DataGroupRedactor dataGroupRedactor,
			MetadataMatchDataFactory matchFactory, DataGroupWrapperFactory wrapperFactory) {
		this.metadataHolder = metadataHolder;
		this.dataGroupRedactor = dataGroupRedactor;
		this.matchFactory = matchFactory;
		this.wrapperFactory = wrapperFactory;
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

		// TODO: attribut?? Om det finns 2 barn med samma nameInData?
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
		possiblyReplaceChildren(originalDataGroup, updatedDataGroup, constraints, permissions,
				metadataGroup);
		return updatedDataGroup;

	}

	private void possiblyReplaceChildren(DataGroup originalDataGroup, DataGroup updatedDataGroup,
			Set<Constraint> constraints, Set<String> permissions, MetadataGroup metadataGroup) {
		DataGroupWrapper wrappedUpdated = wrapperFactory.factor(updatedDataGroup);

		DataGroup redactedDataGroup = dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup, wrappedUpdated,
						constraints, permissions);

		Map<String, List<List<DataAttribute>>> replacedChildren = wrappedUpdated
				.getRemovedNameInDatas();

		for (MetadataChildReference metadataChildReference : metadataGroup.getChildReferences()) {
			if (isMetadataGroup(metadataChildReference) && repeatMaxIsOne(metadataChildReference)) {
				MetadataGroup childMetadataGroup = getMetadataChildFromMetadataHolder(
						metadataChildReference);
				// List<String> attributeReferences = childMetadataGroup.getAttributeReferences();

				// TODO:hur hantera om någon är null?
				DataElement matchingDataInOriginal = getMatchingData(originalDataGroup,
						childMetadataGroup);
				DataElement matchingDataInUpdated = getMatchingData(redactedDataGroup,
						childMetadataGroup);
				if (matchingDataInUpdated != null) {
					String metadataNameInData = matchingDataInUpdated.getNameInData();
					if (childCanBeReplaced(replacedChildren, matchingDataInUpdated,
							metadataNameInData)) {
						possiblyReplaceChildren((DataGroup) matchingDataInOriginal,
								(DataGroup) matchingDataInUpdated, constraints, permissions,
								childMetadataGroup);
					}
				}
			}
		}
		// return redactedDataGroup;
	}

	private boolean childCanBeReplaced(Map<String, List<List<DataAttribute>>> replacedNameInDatas,
			DataElement matchingDataInUpdated, String metadataNameInData) {
		return noneReplacedNameInDataFound(replacedNameInDatas, matchingDataInUpdated,
				metadataNameInData);
	}

	private boolean noneReplacedNameInDataFound(
			Map<String, List<List<DataAttribute>>> replacedNameInDatas,
			DataElement matchingDataInUpdated, String metadataNameInData) {

		if (!replacedNameInDatas.containsKey(metadataNameInData)) {
			return true;
		}
		List<List<DataAttribute>> replacedNameInDataAttributes = replacedNameInDatas
				.get(metadataNameInData);
		Collection<DataAttribute> attributesInChild = matchingDataInUpdated.getAttributes();

		return (!attributesMatch(replacedNameInDataAttributes, attributesInChild));
	}

	private DataElement getMatchingData(DataGroup dataGroup, MetadataGroup metadataGroup) {
		String metadataNameInData = metadataGroup.getNameInData();

		List<DataElement> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData(metadataNameInData);

		for (DataElement childDataElement : allChildrenWithNameInData) {
			MetadataMatchData matcher = matchFactory.factor();
			ValidationAnswer answer = matcher.metadataSpecifiesData(metadataGroup,
					childDataElement);
			if (answer.dataIsValid()) {
				return childDataElement;

			}
		}
		return null;
	}

	private boolean attributesMatch(List<List<DataAttribute>> replacedNameInDataAttributes,
			Collection<DataAttribute> attributesInChildToCompare) {

		// nameGroup nameInData - organisationName
		// type=default, type2=normal, type3=extra
		// type=alternative
		// empty

		// om inga barn i attributesInChild (null eller empty)
		// hitta en tom lista i attributesForReplaced
		// om inte finns - false
		// om finns - true

		if (childToCompareHasNoAttributes(attributesInChildToCompare)) {
			for (List<DataAttribute> attributesList : replacedNameInDataAttributes) {
				if (attributesList.isEmpty()) {
					return true;
				}
			}

		} else {
			// if one list is matching childAttributes
			// return true
			for (List<DataAttribute> replacedAttributes : replacedNameInDataAttributes) {
				if (attributesInListMatch(attributesInChildToCompare, replacedAttributes)) {
					return true;
				}
			}

		}

		return false;
	}

	private boolean attributesInListMatch(Collection<DataAttribute> attributesInChildToCompare,
			List<DataAttribute> replacedAttributes) {
		if (attributesInChildToCompare.size() != replacedAttributes.size()) {
			return false;
		}
		for (DataAttribute childAttribute : attributesInChildToCompare) {

			boolean attributeMatchFound = replacedAttributes.stream()
					.anyMatch(filterByNameAndValue(childAttribute));
			if (!attributeMatchFound) {
				return false;
			}
		}
		return true;

	}

	private Predicate<DataAttribute> filterByNameAndValue(DataAttribute childAttribute) {
		return replacedAttribute -> dataAttributeIsSame(replacedAttribute, childAttribute);
	}

	private boolean dataAttributeIsSame(DataAttribute replacedAttribute,
			DataAttribute childAttribute) {
		return childAttribute.getNameInData().equals(replacedAttribute.getNameInData())
				&& childAttribute.getValue().equals(replacedAttribute.getValue());
	}

	private boolean childToCompareHasNoAttributes(Collection<DataAttribute> attributesInChild) {
		return attributesInChild.isEmpty();
	}
}
