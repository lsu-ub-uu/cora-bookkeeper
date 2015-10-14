package se.uu.ub.cora.metadataformat.linkcollector;

import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;

	public DataRecordLinkCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectLinks(String metadataId, DataGroup dataGroup, String fromRecordType,
			String fromRecordId) {
		getMetadataFromStorage();

		DataGroup collectedDataLinks = DataGroup.withNameInData("collectedDataLinks");
		// TODO: call top level group collector here
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder,
				fromRecordType, fromRecordId);
		List<DataGroup> collectedLinks = collector.collectLinks(metadataId, dataGroup);
		for (DataGroup collectedLink : collectedLinks) {
			collectedDataLinks.addChild(collectedLink);
		}
		return collectedDataLinks;
	}

	private void getMetadataFromStorage() {
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
}
