/*
 * Copyright 2017 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.searchtermcollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

public class DataGroupTermCollectorImp implements DataGroupTermCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;
	private CollectTermHolder collectTermHolder;

	private DataGroup collectedData;
	private List<DataGroup> collectedTerms = new ArrayList<>();

	public DataGroupTermCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public DataGroup collectTerms(String metadataGroupId, DataGroup dataGroup) {
		populateMetadataHolderFromMetadataStorage();
		populateCollectTermHolderFromMetadataStorage();
		collectTermsFromDataUsingMetadata(metadataGroupId, dataGroup);
		return createCollectedData(dataGroup);
	}

	private void populateMetadataHolderFromMetadataStorage() {
		metadataHolder = new MetadataHolder();
		Collection<DataGroup> metadataElementDataGroups = metadataStorage.getMetadataElements();
		convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(metadataElementDataGroups);
	}

	private void convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(
			Collection<DataGroup> metadataElements) {
		for (DataGroup metadataElement : metadataElements) {
			convertDataGroupToMetadataElementAndAddItToMetadataHolder(metadataElement);
		}
	}

	private void convertDataGroupToMetadataElementAndAddItToMetadataHolder(
			DataGroup metadataElement) {
		DataGroupToMetadataConverterFactory factory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(metadataElement);
		DataGroupToMetadataConverter converter = factory.factor();
		metadataHolder.addMetadataElement(converter.toMetadata());
	}

	private void populateCollectTermHolderFromMetadataStorage() {
		collectTermHolder = new CollectTermHolder();
		for (DataGroup collectTerm : metadataStorage.getCollectTerms()) {
			collectTermHolder.addCollectTerm(collectTerm);
		}
	}

	private void collectTermsFromDataUsingMetadata(String metadataGroupId, DataGroup dataGroup) {
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectTermsFromDataUsingMetadataChildren(metadataChildReferences, dataGroup);
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectTermsFromDataUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences, DataGroup dataGroup) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectDataForMetadataChildIfItHasCollectTerm(metadataChildReference, dataGroup);
			recurseAndCollectTermsFromChildsGroupChildren(dataGroup, metadataChildReference);
		}
	}

	private void recurseAndCollectTermsFromChildsGroupChildren(DataGroup dataGroup,
			MetadataChildReference metadataChildReference) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		if (isMetadataGroup(childMetadataElement)) {
			recurseAndCollectTermFromChildsGroupChildren(dataGroup, childMetadataElement);
		}
	}

	private void recurseAndCollectTermFromChildsGroupChildren(DataGroup dataGroup,
			MetadataElement childMetadataElement) {
		String childMetadataGroupId = childMetadataElement.getId();
		for (DataElement childDataElement : dataGroup.getChildren()) {
			if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
				collectTermsFromDataUsingMetadata(childMetadataGroupId,
						(DataGroup) childDataElement);
			}
		}
	}

	private boolean isMetadataGroup(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof MetadataGroup;
	}

	private void collectDataForMetadataChildIfItHasCollectTerm(
			MetadataChildReference metadataChildReference, DataGroup dataGroup) {
		if (childReferenceHasCollectTerms(metadataChildReference)) {
			collectTermsFromDataGroupUsingMetadataChild(metadataChildReference, dataGroup);
		}
	}

	private boolean childReferenceHasCollectTerms(MetadataChildReference metadataChildReference) {
		return !metadataChildReference.getCollectIndexTerms().isEmpty();
	}

	private void collectTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference, DataGroup dataGroup) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		collectTermsFromDataGroupChildren(childMetadataElement,
				metadataChildReference.getCollectIndexTerms(), dataGroup);
	}

	private void collectTermsFromDataGroupChildren(MetadataElement childMetadataElement,
			List<String> collectTerms, DataGroup dataGroup) {
		for (DataElement childDataElement : dataGroup.getChildren()) {
			collectTermsFromDataGroupChild(childMetadataElement, childDataElement, collectTerms);
		}
	}

	private void collectTermsFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement childDataElement, List<String> collectTerms) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			possiblyCreateCollectedTerm(childDataElement, collectTerms);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataElement dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void possiblyCreateCollectedTerm(DataElement childDataElement,
			List<String> collectTerms) {
		if (childDataElement instanceof DataAtomic) {
			createCollectTerm(childDataElement, collectTerms);
		}
	}

	private void createCollectTerm(DataElement childDataElement, List<String> collectTerms) {
		for (String collectTermId : collectTerms) {
			String childDataElementValue = ((DataAtomic) childDataElement).getValue();
			possiblyCreateAndAddCollectedTerm(collectTermId, childDataElementValue);
		}
	}

	private void possiblyCreateAndAddCollectedTerm(String collectTermId,
			String childDataElementValue) {
		DataGroup collectTerm = collectTermHolder.getCollectTerm(collectTermId);
		createAndAddCollectedTerm(childDataElementValue, collectTerm);
	}

	private void createAndAddCollectedTerm(String childDataElementValue, DataGroup collectTerm) {
		String collectTermId = getCollectTermId(collectTerm);
		String collectTermType = collectTerm.getAttribute("type");
		DataGroup collectedTerm = createCollectedTerm(childDataElementValue, collectTermId,
				collectTermType, collectTerm);
		collectedTerms.add(collectedTerm);
	}

	private String getCollectTermId(DataGroup collectTerm) {
		DataGroup recordInfo = collectTerm.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private DataGroup createCollectedTerm(String childDataElementValue, String collectTermId,
			String collectType, DataGroup collectTerm) {
		DataGroup collectedTerm = createCollectTermDataGroupWithType(collectType);
		createAndAddCollectTermName(collectTermId, collectedTerm);
		createAndAddCollectTermValue(childDataElementValue, collectedTerm);
		addExtraData(collectTerm, collectedTerm);
		return collectedTerm;
	}

	private DataGroup createCollectTermDataGroupWithType(String collectType) {
		DataGroup collectedTerm = DataGroup.withNameInData("collectTerm");
		collectedTerm.addAttributeByIdWithValue("type", collectType);
		return collectedTerm;
	}

	private void addExtraData(DataGroup collectTerm, DataGroup collectedTerm) {
		DataGroup extraData = collectTerm.getFirstGroupWithNameInData("extraData");
		collectedTerm.addChild(extraData);
	}

	private void createAndAddCollectTermName(String collectTermNameInData,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermName = DataAtomic.withNameInDataAndValue("collectTermId",
				collectTermNameInData);
		collectedSearchTerm.addChild(searchTermName);
	}

	private void createAndAddCollectTermValue(String childDataElementValue,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermValue = DataAtomic.withNameInDataAndValue("collectTermValue",
				childDataElementValue);
		collectedSearchTerm.addChild(searchTermValue);
	}

	private DataGroup createCollectedData(DataGroup dataGroup) {
		collectedData = DataGroup.withNameInData("collectedData");
		extractTypeFromDataGroupAndSetInCollectedData(dataGroup);
		extractIdFromDataGroupAndSetInCollectedData(dataGroup);
		addCollectedTermsToCollectedData();
		return collectedData;
	}

	private void addCollectedTermsToCollectedData() {
		if (!collectedTerms.isEmpty()) {
			int counter = 0;
			DataGroup index = DataGroup.withNameInData("index");
			collectedData.addChild(index);
			for (DataGroup collectedSearchTerm : collectedTerms) {
				collectedSearchTerm.setRepeatId(String.valueOf(counter));
				index.addChild(collectedSearchTerm);
				counter++;
			}
		}
	}

	private void extractTypeFromDataGroupAndSetInCollectedData(DataGroup dataGroup) {
		String type = extractTypeFromDataGroup(dataGroup);
		collectedData.addChild(DataAtomic.withNameInDataAndValue("type", type));
	}

	private String extractTypeFromDataGroup(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup typeGroup = recordInfo.getFirstGroupWithNameInData("type");
		return typeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void extractIdFromDataGroupAndSetInCollectedData(DataGroup dataGroup) {
		String id = getCollectTermId(dataGroup);
		collectedData.addChild(DataAtomic.withNameInDataAndValue("id", id));
	}

}
