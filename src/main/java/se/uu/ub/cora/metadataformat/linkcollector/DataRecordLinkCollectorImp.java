package se.uu.ub.cora.metadataformat.linkcollector;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorage metadataStorage;

	public DataRecordLinkCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectLinks(String recordType, String recordId, String metadataId,
			DataGroup dataGroup) {
		return DataGroup.withNameInData("collectedDataLinks");
	}

}
