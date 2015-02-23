package epc.metadataformat;


/**
 * CoherentMetadata is a class that works as a container around all metadata in
 * the system. It can hold texts, metadataElemnts, Collections, Presentations
 * etc. This holder makes it possible to get a version of all metadata that can
 * be fetched in one transaction as to get a consistent state, from storage.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class CoherentMetadata {

	private TextHolder textHolder = new TextHolder();
	private MetadataHolder metadataHolder = new MetadataHolder();

	/**
	 * This empty constructor uses the default maps to store texts and metadata
	 */
	public CoherentMetadata() {
		//This constructor makes it possible to uses the default maps 
	}
	
	public CoherentMetadata(TextHolder textHolder,
			MetadataHolder metadataHolder) {
		if (null == textHolder) {
			throw new IllegalArgumentException("textHolder must not be null");
		}
		if (null == metadataHolder) {
			throw new IllegalArgumentException(
					"metadataHolder must not be null");
		}
		this.textHolder = textHolder;
		this.metadataHolder = metadataHolder;

	}

	public MetadataHolder getMetadataElements() {
		return metadataHolder;
	}

	public TextHolder getTextElements() {
		return textHolder;
	}

	public String getTextTranslation(String languageId, String textId) {
		return textHolder.getTextElement(textId).getTranslationByLanguage(languageId);
	}

}
