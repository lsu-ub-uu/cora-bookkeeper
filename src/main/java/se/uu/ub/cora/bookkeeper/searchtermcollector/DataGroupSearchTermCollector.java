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
import se.uu.ub.cora.bookkeeper.metadata.*;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

public class DataGroupSearchTermCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;
	private SearchTermHolder searchTermHolder;

	private DataGroup searchData;
	private List<DataGroup> collectedSearchTerms = new ArrayList<>();
	private DataGroup dataGroup;


	public DataGroupSearchTermCollector(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectSearchTerms(String metadataGroupId, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		populateMetadataHolderFromMetadataStorage();
		populateSearchTermHolderFromMetadataStorage();
		collectSearchTermsFromDataUsingMetadata(metadataGroupId);
		return createSearchDataIfSearchTermsExist(dataGroup);
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

	private void populateSearchTermHolderFromMetadataStorage(){
		searchTermHolder = new SearchTermHolder();
		for(DataGroup searchTerm : metadataStorage.getSearchTerms()){
			searchTermHolder.addSearchTerm(searchTerm);
		}
	}

	private void collectSearchTermsFromDataUsingMetadata(String metadataGroupId) {
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectSearchTermsFromDataGroupUsingMetadataChildren(metadataChildReferences);
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectSearchTermsFromDataGroupUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			possiblyCollectSearchTermsFromDataGroupUsingMetadataChild(metadataChildReference);
		}
	}

	private void possiblyCollectSearchTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference) {
		if (childReferenceHasSearchTerms(metadataChildReference)) {
			collectSearchTermsFromDataGroupUsingMetadataChild(metadataChildReference);
		}
	}

	private boolean childReferenceHasSearchTerms(MetadataChildReference metadataChildReference) {
		return !metadataChildReference.getSearchTerms().isEmpty();
	}

	private void collectSearchTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		collectSearchTermsFromDataGroupChildren(childMetadataElement,
				metadataChildReference.getSearchTerms());
	}

	private void collectSearchTermsFromDataGroupChildren(MetadataElement childMetadataElement,
			List<String> searchTerms) {
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
		DataGroup searchTerm = searchTermHolder.getSearchTerm(metadataSearchTermId);
		createAndAddCollectedSearchTerm(childDataElementValue, searchTerm);
	}

	private String getSearchTermId(DataGroup searchTerm) {
		DataGroup recordInfo = searchTerm.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void createAndAddCollectedSearchTerm(String childDataElementValue,
			DataGroup searchTerm) {
		String searchFieldNameInData = getSearchFieldNameInDataFromSearchTerm(searchTerm);

		DataGroup collectedSearchTerm = createCollectedSearchTerm(childDataElementValue,
				searchFieldNameInData, searchTerm);
		collectedSearchTerms.add(collectedSearchTerm);
	}

	private String getSearchFieldNameInDataFromSearchTerm(DataGroup searchTerm) {
		String linkedMetadata = getMetadataIdFromSearchFieldRef(searchTerm);
		MetadataElement metadataElement = metadataHolder.getMetadataElement(linkedMetadata);
		return metadataElement.getNameInData();
	}

	private String getMetadataIdFromSearchFieldRef(DataGroup term) {
		DataGroup searchFieldRef = term.getFirstGroupWithNameInData("searchFieldRef");
		return searchFieldRef.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private DataGroup createCollectedSearchTerm(String childDataElementValue,
												String searchFiledNameInData, DataGroup searchTerm) {
		DataGroup collectedSearchTerm = DataGroup.withNameInData("searchTerm");
		createAndAddSearchTermName(searchFiledNameInData, collectedSearchTerm);
		createAndAddSearchTermValue(childDataElementValue, collectedSearchTerm);
		addIndexTypes(searchTerm, collectedSearchTerm);
		return collectedSearchTerm;
	}

	private void addIndexTypes(DataGroup searchTerm, DataGroup collectedSearchTerm) {
		Collection<DataAtomic> indexTypes = searchTerm.getAllDataAtomicsWithNameInData("indexType");
		for(DataAtomic indexType : indexTypes){
			collectedSearchTerm.addChild(indexType);
		}
	}

	private void createAndAddSearchTermName(String searchFiledNameInData,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermName = DataAtomic.withNameInDataAndValue("searchTermName",
				searchFiledNameInData);
		collectedSearchTerm.addChild(searchTermName);
	}

	private void createAndAddSearchTermValue(String childDataElementValue,
			DataGroup collectedSearchTerm) {
		DataAtomic searchTermValue = DataAtomic.withNameInDataAndValue("searchTermValue",
				childDataElementValue);
		collectedSearchTerm.addChild(searchTermValue);
	}

	private DataGroup createSearchDataIfSearchTermsExist(DataGroup dataGroup) {
		if (collectedSearchTerms.isEmpty()) {
			return null;
		}
		return createSearchData(dataGroup);
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
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String type = recordInfo.getFirstAtomicValueWithNameInData("type");
		searchData.addChild(DataAtomic.withNameInDataAndValue("type", type));
	}

	private void extractIdFromDataGroupAndSetInSearchData(DataGroup dataGroup) {
		String id = getSearchTermId(dataGroup);
		searchData.addChild(DataAtomic.withNameInDataAndValue("id", id));
	}

}
