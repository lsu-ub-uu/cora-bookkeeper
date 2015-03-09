package epc.metadataformat.getmetadata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.storage.MetadataStorage;

public final class MetadataGetterImp implements MetadataGetter {

	private final MetadataStorage metadataStorage;

	public static MetadataGetterImp usingMetadataStorage(MetadataStorage metadataStorage) {
		return new MetadataGetterImp(metadataStorage);
	}

	private MetadataGetterImp(MetadataStorage metadataStorage) {
		throwErrorIfEmptyConstructorArgument(metadataStorage);
		this.metadataStorage = metadataStorage;
	}

	private void throwErrorIfEmptyConstructorArgument(MetadataStorage metadataStorage) {
		if (null == metadataStorage) {
			throw new IllegalArgumentException("metadataStorage must not be null");
		}
	}

	@Override
	public CoherentMetadata getAllMetadata() {
		return metadataStorage.getAllMetadata();
	}

}
