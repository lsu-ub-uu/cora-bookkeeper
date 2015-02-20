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
	private CoherentMetadata coherentMetadata = new CoherentMetadata();

	/**
	 * This MetadataInMemoryStorage constructor creates a new empty memory based
	 * storage.
	 */
	public MetadataInMemoryStorage() {
		//This constructor makes it possible to uses the default CoherentMetadata
	}

	/**
	 * This MetadataInMemoryStorage constructor creates a new memory based
	 * storage using the entered coherentMetadata as storage.
	 * 
	 * @param coherentMetadata
	 *            A coherentMetadata to use as storage for this
	 *            MetadataInMemoryStorage
	 */
	public MetadataInMemoryStorage(CoherentMetadata coherentMetadata) {
		if (null == coherentMetadata) {
			throw new IllegalArgumentException("coherentMetadata must not be null");
		}
		this.coherentMetadata = coherentMetadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoherentMetadata getAllMetadata() {
		return coherentMetadata;
	}

}
