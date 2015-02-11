package epc.metadataformat.storage;

import epc.metadataformat.MetadataHolder;

/**
 * MetadataLoader loads metadata from storage using an injected
 * MetadataStorageGateway
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataLoader {

	private final MetadataStorageGateway metadataStorageGateway;

	public MetadataLoader(MetadataStorageGateway metadataStorageGateway) {
		this.metadataStorageGateway = metadataStorageGateway;
	}

	public MetadataHolder loadAllMetadata() {
		return metadataStorageGateway.loadAllMetadata();
	}
}
