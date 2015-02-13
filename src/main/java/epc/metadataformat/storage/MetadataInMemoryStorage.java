package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataStorageInMemory is a memory implementation of MetadataStorageGateway,
 * its intended use is mainly for testing.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataInMemoryStorage implements MetadataStorageGateway {
	private final CoherentMetadata coherentMetadata;

	public MetadataInMemoryStorage(CoherentMetadata coherentMetadata) {
		this.coherentMetadata = coherentMetadata;
	}

	@Override
	public CoherentMetadata loadAllMetadata() {
		return coherentMetadata;
	}

}
