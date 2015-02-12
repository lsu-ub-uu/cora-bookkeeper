package epc.metadataformat.getmetadata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.storage.MetadataCache;

public class GetMetadata implements GetMetadataInputBoundry {

	private MetadataCache metadataCache;

	public GetMetadata(MetadataCache metadataCache) {
		this.metadataCache = metadataCache;
	}

	@Override
	public CoherentMetadata getAllMetadata() {
		return metadataCache.getAllMetadata();
	}


}
