package se.uu.ub.cora.bookkeeper.searchtermcollector;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

public class DataGroupSearchTermCollector {

	private MetadataHolder metadataHolder;

	private List<DataGroup> searchTerms;

	private DataGroup dataGroup;

	public DataGroupSearchTermCollector(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	public List<DataGroup> collectSearchTerms(String metadataGroupId, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		searchTerms = new ArrayList();
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectSearchTermsFromDataGroupUsingMetadataChildren(metadataChildReferences);
		return searchTerms;
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
			collectSearchTermsFromDataGroupChildren(childMetadataElement);
			// TODO: måste skicka ner searchterms
		}
	}

	private void collectSearchTermsFromDataGroupChildren(MetadataElement childMetadataElement) {
		for (DataElement childDataElement : dataGroup.getChildren()) {
			collectSearchTermsFromDataGroupChild(childMetadataElement, childDataElement);
		}
	}

	private void collectSearchTermsFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement childDataElement) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			possiblyCreateSearchTerm(childMetadataElement, childDataElement);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataElement dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void possiblyCreateSearchTerm(MetadataElement childMetadataElement,
			DataElement childDataElement) {

	}
}
