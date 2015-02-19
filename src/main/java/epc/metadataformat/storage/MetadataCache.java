package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataLoader loads metadata from storage using an injected
 * MetadataStorageGateway. This cache automatically populates itself on startup
 * using the MetadataStorageGateway.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataCache {

	private CoherentMetadata coherentMetadata = new CoherentMetadata();

	public MetadataCache() {
	}

	/**
	 * Constructor, loads all metadata into this cache using the injected
	 * MetadataStorageGateway
	 * 
	 * @param metadataStorageGateway
	 */
	public MetadataCache(MetadataStorageGateway metadataStorageGateway) {
		if (null != metadataStorageGateway) {
			coherentMetadata = metadataStorageGateway.getAllMetadata();
		}
	}

	/**
	 * getTextTranslation returns the requested text using the requested
	 * language
	 * 
	 * @param languageId
	 *            A String with languageI such as "sv"
	 * @param textId
	 *            A String with the textId
	 * @return A String with the requested translation
	 */
	public String getTextTranslation(String languageId, String textId) {
		return coherentMetadata.getTextTranslation(languageId, textId);
	}

	/**
	 * getAllMetadata returns all metadata for the whole system, as a
	 * CoherentMetadata populated with metadataFormat, Presentation, Collections
	 * and Texts
	 * 
	 * @return A CoherentMetadata with all metadata for the entire system
	 */
	public CoherentMetadata getAllMetadata() {
		return coherentMetadata;
	}
}
