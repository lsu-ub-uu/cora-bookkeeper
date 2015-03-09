package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataStorageInMemory is a memory implementation of MetadataStorage, its intended use is mainly
 * for testing.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataStorageInMemory implements MetadataStorage {

	private CoherentMetadata coherentMetadata = new CoherentMetadata();

	/**
	 * This MetadataStorageInMemory constructor creates a new empty memory based storage.
	 */
	public MetadataStorageInMemory() {
		// This constructor makes it possible to uses the default CoherentMetadata
	}

	public static MetadataStorageInMemory usingCoherentMetadata(CoherentMetadata coherentMetadata) {
		return new MetadataStorageInMemory(coherentMetadata);
	}

	private MetadataStorageInMemory(CoherentMetadata coherentMetadata) {
		throwErrorIfEmptyConstructorArgument(coherentMetadata);
		this.coherentMetadata = coherentMetadata;
	}

	private void throwErrorIfEmptyConstructorArgument(CoherentMetadata coherentMetadata) {
		if (null == coherentMetadata) {
			throw new IllegalArgumentException("coherentMetadata must not be null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoherentMetadata getAllMetadata() {
		return coherentMetadata;
	}

}
