package epc.metadataformat.getmetadata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.storage.MetadataStorageGateway;

public class MetadataStorageGetter implements MetadataStorageGetterInputBoundry {


	private final MetadataStorageGateway metadataStorageGateway;

	public MetadataStorageGetter(MetadataStorageGateway metadataStorageGateway) {
		this.metadataStorageGateway = metadataStorageGateway;
		if(null==metadataStorageGateway){
			throw new IllegalArgumentException("metadataStorageGateway must not be null");
		}
	}

	@Override
	public CoherentMetadata getAllMetadata() {
		return metadataStorageGateway.getAllMetadata();
	}

}
