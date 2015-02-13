package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataLoader loads metadata from storage using an injected
 * MetadataStorageGateway
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataCache {

	private final CoherentMetadata coherentMetadata;

	public MetadataCache(MetadataStorageGateway metadataStorageGateway) {
		coherentMetadata = metadataStorageGateway.loadAllMetadata();
	}

	public String getTextTranslation(String languageId, String textId) {
		return coherentMetadata.getTextTranslation(languageId, textId);
	}

	public CoherentMetadata getAllMetadata() {
		return coherentMetadata;
	}
}
