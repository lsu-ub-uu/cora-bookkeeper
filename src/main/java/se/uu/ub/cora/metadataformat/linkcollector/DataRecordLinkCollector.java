package se.uu.ub.cora.metadataformat.linkcollector;

import se.uu.ub.cora.metadataformat.data.DataGroup;

public interface DataRecordLinkCollector {

	DataGroup collectLinks(String recordType, String RecordId, String metadataId,
			DataGroup dataGroup);

}
