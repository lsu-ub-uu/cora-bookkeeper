package se.uu.ub.cora.metadataformat.linkcollector;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;
import se.uu.ub.cora.metadataformat.metadata.MetadataChildReference;
import se.uu.ub.cora.metadataformat.metadata.MetadataElement;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;

public class DataGroupRecordLinkCollector {

	private MetadataHolder metadataHolder;
	private String fromRecordType;
	private String fromRecordId;
	private List<DataGroup> linkList;
	private DataGroup dataGroup;

	public DataGroupRecordLinkCollector(MetadataHolder metadataHolder, String fromRecordType,
			String fromRecordId) {
		this.metadataHolder = metadataHolder;
		this.fromRecordType = fromRecordType;
		this.fromRecordId = fromRecordId;
	}

	public List<DataGroup> collectLinks(String metadataId, DataGroup dataGroup) {

		this.dataGroup = dataGroup;
		linkList = new ArrayList<>();

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();
		collectLinksFromDataGroupUsingMetadataChildren(metadataChildReferences);

		return linkList;
	}

	private void collectLinksFromDataGroupUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectLinksFromDataGroupUsingMetadataChild(metadataChildReference);
		}
	}

	private void collectLinksFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference) {
		MetadataElement childMetadataElement = metadataHolder
				.getMetadataElement(metadataChildReference.getReferenceId());
		if (isMetadataElementConcerningLinks(childMetadataElement)) {
			findDataGroupAndCollectLinks(childMetadataElement);
		}
	}

	private boolean isMetadataElementConcerningLinks(MetadataElement childMetadataElement) {
		return isDataToDataLink(childMetadataElement)
				|| childMetadataElement instanceof MetadataGroup;
	}

	private void findDataGroupAndCollectLinks(MetadataElement childMetadataElement) {
		for (DataElement dataElement : dataGroup.getChildren()) {
			collectLinksFromDataGroupChild(childMetadataElement, dataElement);
		}
	}

	private void collectLinksFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement dataElement) {
		if (childDataIsSpecifiedByMetadata(childMetadataElement, dataElement)) {
			createLinkOrParseChildGroup(childMetadataElement, dataElement);
		}
	}

	private boolean childDataIsSpecifiedByMetadata(MetadataElement childMetadataElement,
			DataElement dataElement) {
		String dataNameInData = dataElement.getNameInData();
		String metadataNameInData = childMetadataElement.getNameInData();
		return dataNameInData.equals(metadataNameInData);
	}

	private void createLinkOrParseChildGroup(MetadataElement childMetadataElement,
			DataElement dataElement) {
		if (isDataToDataLink(childMetadataElement)) {
			collectRecordToRecordLink(dataElement);
		} else {
			// go through subGroup
		}
	}

	private boolean isDataToDataLink(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof DataToDataLink;
	}

	private void collectRecordToRecordLink(DataElement dataElement) {
		// create link
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");
		linkList.add(recordToRecordLink);
		createFromPart(recordToRecordLink);
		createToPart(dataElement, recordToRecordLink);
	}

	private void createFromPart(DataGroup recordToRecordLink) {
		DataRecordLink from = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("from",
				fromRecordType, fromRecordId);
		recordToRecordLink.addChild(from);
	}

	private void createToPart(DataElement dataElement, DataGroup recordToRecordLink) {
		DataRecordLink dataRecordLink = (DataRecordLink) dataElement;
		DataRecordLink to = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("to",
				dataRecordLink.getRecordType(), dataRecordLink.getRecordId());
		recordToRecordLink.addChild(to);
	}

}
