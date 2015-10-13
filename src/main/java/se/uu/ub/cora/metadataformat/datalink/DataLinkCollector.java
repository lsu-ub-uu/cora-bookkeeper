package se.uu.ub.cora.metadataformat.datalink;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;

public class DataLinkCollector {

	private MetadataStorage metadataStorage;

	public DataLinkCollector(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectLinks(String recordType, String recordId, String metadataId,
			DataGroup dataGroup) {
		return DataGroup.withNameInData("collectedDataLinks");
	}

}
