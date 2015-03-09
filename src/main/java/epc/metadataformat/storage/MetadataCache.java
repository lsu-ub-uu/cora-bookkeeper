package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataLoader loads metadata from storage using an injected MetadataStorage. This cache
 * automatically populates itself on startup using the MetadataStorage.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataCache {

	private CoherentMetadata coherentMetadata = new CoherentMetadata();

	/**
	 * This default constructor uses the default CoherentMetadata as cache
	 */
	public MetadataCache() {
		// This constructor makes it possible to uses the default
		// CoherentMetadata as cache
	}

	public static MetadataCache usingMetadataStorage(MetadataStorage metadataStorage) {
		return new MetadataCache(metadataStorage);
	}

	private MetadataCache(MetadataStorage metadataStorage) {
		if (null != metadataStorage) {
			coherentMetadata = metadataStorage.getAllMetadata();
		}
	}

	/**
	 * getTextTranslation returns the requested text using the requested language
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
	 * getAllMetadata returns all metadata for the whole system, as a CoherentMetadata populated
	 * with metadataFormat, Presentation, Collections and Texts
	 * 
	 * @return A CoherentMetadata with all metadata for the entire system
	 */
	public CoherentMetadata getAllMetadata() {
		return coherentMetadata;
	}
}
