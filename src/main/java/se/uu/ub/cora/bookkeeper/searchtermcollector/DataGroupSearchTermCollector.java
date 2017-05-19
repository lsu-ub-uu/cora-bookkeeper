package se.uu.ub.cora.bookkeeper.searchtermcollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

public class DataGroupSearchTermCollector {

	private MetadataHolder metadataHolder;

	private DataGroup searchData;
	private List<DataGroup> collectedSearchTerms = new ArrayList<>();

	private DataGroup dataGroup;

	private MetadataStorage metadataStorage;

	public DataGroupSearchTermCollector(MetadataHolder metadataHolder,
			MetadataStorage metadataStorage) {
		this.metadataHolder = metadataHolder;
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectSearchTerms(String metadataGroupId, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectSearchTermsFromDataGroupUsingMetadataChildren(metadataChildReferences);
		if (collectedSearchTerms.isEmpty()) {
			return null;
		}
		searchData = DataGroup.withNameInData("searchData");
		extractTypeFromDataGroupAndSetInSearchData(dataGroup);
		extractIdFromDataGroupAndSetInSearchData(dataGroup);
		for (DataGroup collectedSearchTerm : collectedSearchTerms) {
			searchData.addChild(collectedSearchTerm);
		}
		return searchData;
	}

	private void extractTypeFromDataGroupAndSetInSearchData(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String type = recordInfo.getFirstAtomicValueWithNameInData("type");
		searchData.addChild(DataAtomic.withNameInDataAndValue("type", type));
	}

	private void extractIdFromDataGroupAndSetInSearchData(DataGroup dataGroup) {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		searchData.addChild(DataAtomic.withNameInDataAndValue("id", id));
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectSearchTermsFromDataGroupUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectSearchTermsFromDataGroupUsingMetadataChild(metadataChildReference);
		}
	}

	private void collectSearchTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference) {
		if (!metadataChildReference.getSearchTerms().isEmpty()) {

			String referenceId = metadataChildReference.getLinkedRecordId();

			MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
			collectSearchTermsFromDataGroupChildren(childMetadataElement,
					metadataChildReference.getSearchTerms());
		}
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
			createSearchTerm(childMetadataElement, childDataElement, searchTerms);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataElement dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void createSearchTerm(MetadataElement childMetadataElement,
			DataElement childDataElement, List<String> searchTerms) {
		DataGroup searchTerm = DataGroup.withNameInData("searchTerm");
		DataAtomic name = DataAtomic.withNameInDataAndValue("searchTermName", searchTerms.get(0));
		searchTerm.addChild(name);
		if (childDataElement instanceof DataAtomic) {
			// titleSearchTerm = dataValue
			// hämta ut searchTerm metadataStorage.getSearchTerms
			Collection<DataGroup> searchTermsCollection = metadataStorage.getSearchTerms();
			// hämta searchTitleTextVar för att plocka ut det nameInData
			String dataValue = ((DataAtomic) childDataElement).getValue();
			DataAtomic value = DataAtomic.withNameInDataAndValue("searchTermValue", dataValue);
			searchTerm.addChild(value);
			collectedSearchTerms.add(searchTerm);
		}

	}
}
