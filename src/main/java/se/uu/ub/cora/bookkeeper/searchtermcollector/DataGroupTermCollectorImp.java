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

	private DataGroup searchData;
	private List<DataGroup> collectedSearchTerms = new ArrayList<>();

	public DataGroupTermCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public DataGroup collectTerms(String metadataGroupId, DataGroup dataGroup) {
		populateMetadataHolderFromMetadataStorage();
		populateCollectTermHolderFromMetadataStorage();
		collectSearchTermsFromDataUsingMetadata(metadataGroupId, dataGroup);
		return createSearchData(dataGroup);
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
			collectTermHolder.addSearchTerm(collectTerm);
		}
	}

	private void collectSearchTermsFromDataUsingMetadata(String metadataGroupId,
			DataGroup dataGroup) {
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectSearchTermsFromDataUsingMetadataChildren(metadataChildReferences, dataGroup);
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectSearchTermsFromDataUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences, DataGroup dataGroup) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectDataForMetadataChildIfItHasCollectTerm(metadataChildReference, dataGroup);
			recurseAndcollectTermsFromChildsGroupChildren(dataGroup, metadataChildReference);
		}
	}

	private void recurseAndcollectTermsFromChildsGroupChildren(DataGroup dataGroup,
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
				collectSearchTermsFromDataUsingMetadata(childMetadataGroupId,
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
			collectSearchTermsFromDataGroupUsingMetadataChild(metadataChildReference, dataGroup);
		}
	}

	private boolean childReferenceHasCollectTerms(MetadataChildReference metadataChildReference) {
		return !metadataChildReference.getCollectIndexTerms().isEmpty();
	}

	private void collectSearchTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference, DataGroup dataGroup) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		collectSearchTermsFromDataGroupChildren(childMetadataElement,
				metadataChildReference.getCollectIndexTerms(), dataGroup);
	}

	private void collectSearchTermsFromDataGroupChildren(MetadataElement childMetadataElement,
			List<String> searchTerms, DataGroup dataGroup) {
		for (DataElement childDataElement : dataGroup.getChildren()) {
			collectSearchTermsFromDataGroupChild(childMetadataElement, childDataElement,
					searchTerms);
		}
	}

	private void collectSearchTermsFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement childDataElement, List<String> searchTerms) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			possiblyCreateSearchTerm(childDataElement, searchTerms);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataElement dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void possiblyCreateSearchTerm(DataElement childDataElement,
			List<String> metadataSearchTerms) {
		if (childDataElement instanceof DataAtomic) {
			createSearchTerm(childDataElement, metadataSearchTerms);
		}
	}

	private void createSearchTerm(DataElement childDataElement, List<String> metadataSearchTerms) {
		for (String metadataSearchTermId : metadataSearchTerms) {
			String childDataElementValue = ((DataAtomic) childDataElement).getValue();
			possiblyCreateAndAddCollectedSearchTerm(metadataSearchTermId, childDataElementValue);
		}
	}

	private void possiblyCreateAndAddCollectedSearchTerm(String metadataSearchTermId,
			String childDataElementValue) {
		DataGroup searchTerm = collectTermHolder.getSearchTerm(metadataSearchTermId);
		createAndAddCollectedSearchTerm(childDataElementValue, searchTerm);
	}

	private void createAndAddCollectedSearchTerm(String childDataElementValue,
			DataGroup searchTerm) {
		String searchTermId = getSearchTermId(searchTerm);

		DataGroup collectedSearchTerm = createCollectedSearchTerm(childDataElementValue,
				searchTermId, searchTerm);
		collectedSearchTerms.add(collectedSearchTerm);
	}

	private String getSearchTermId(DataGroup searchTerm) {
		DataGroup recordInfo = searchTerm.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private DataGroup createCollectedSearchTerm(String childDataElementValue, String searchTermId,
			DataGroup searchTerm) {
		DataGroup collectedSearchTerm = DataGroup.withNameInData("collectedIndexTerm");
		createAndAddSearchTermName(searchTermId, collectedSearchTerm);
		createAndAddSearchTermValue(childDataElementValue, collectedSearchTerm);
		addIndexTypes(searchTerm, collectedSearchTerm);
		return collectedSearchTerm;
	}

	private void addIndexTypes(DataGroup searchTerm, DataGroup collectedSearchTerm) {
		DataGroup extraData = searchTerm.getFirstGroupWithNameInData("extraData");
		Collection<DataAtomic> indexTypes = extraData.getAllDataAtomicsWithNameInData("indexType");
		for (DataAtomic indexType : indexTypes) {
			collectedSearchTerm.addChild(indexType);
		}
	}

	private void createAndAddSearchTermName(String searchFiledNameInData,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermName = DataAtomic.withNameInDataAndValue("searchTermId",
				searchFiledNameInData);
		collectedSearchTerm.addChild(searchTermName);
	}

	private void createAndAddSearchTermValue(String childDataElementValue,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermValue = DataAtomic.withNameInDataAndValue("searchTermValue",
				childDataElementValue);
		collectedSearchTerm.addChild(searchTermValue);
	}

	private DataGroup createSearchData(DataGroup dataGroup) {
		searchData = DataGroup.withNameInData("recordIndexData");
		extractTypeFromDataGroupAndSetInSearchData(dataGroup);
		extractIdFromDataGroupAndSetInSearchData(dataGroup);
		addCollectedSearchTermsToSearchData();
		return searchData;
	}

	private void addCollectedSearchTermsToSearchData() {
		int counter = 0;
		for (DataGroup collectedSearchTerm : collectedSearchTerms) {
			collectedSearchTerm.setRepeatId(String.valueOf(counter));
			searchData.addChild(collectedSearchTerm);
			counter++;
		}
	}

	private void extractTypeFromDataGroupAndSetInSearchData(DataGroup dataGroup) {
		String type = extractTypeFromDataGroup(dataGroup);
		searchData.addChild(DataAtomic.withNameInDataAndValue("type", type));
	}

	private String extractTypeFromDataGroup(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup typeGroup = recordInfo.getFirstGroupWithNameInData("type");
		return typeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void extractIdFromDataGroupAndSetInSearchData(DataGroup dataGroup) {
		String id = getSearchTermId(dataGroup);
		searchData.addChild(DataAtomic.withNameInDataAndValue("id", id));
	}

}
