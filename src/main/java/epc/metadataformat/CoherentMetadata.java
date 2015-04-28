package epc.metadataformat;

import epc.metadataformat.presentation.PresentationHolder;

/**
 * CoherentMetadata is a class that works as a container around all metadata in the system. It can
 * hold texts, metadataElements, Collections, Presentations etc. This holder makes it possible to
 * get a version of all metadata that can be fetched in one transaction as to get a consistent state
 * from storage.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class CoherentMetadata {

	private TextHolder textHolder = new TextHolder();
	private MetadataHolder metadataHolder = new MetadataHolder();
	private PresentationHolder presentationHolder = new PresentationHolder();

	/**
	 * This empty constructor uses the default maps to store texts and metadata
	 */
	public CoherentMetadata() {
		// This constructor makes it possible to uses the default maps
	}

	public static CoherentMetadata usingTextMetadataPresentationHolders(TextHolder textHolder,
			MetadataHolder metadataHolder, PresentationHolder presentationHolder) {
		return new CoherentMetadata(textHolder, metadataHolder, presentationHolder);
	}

	private CoherentMetadata(TextHolder textHolder, MetadataHolder metadataHolder,
			PresentationHolder presentationHolder) {
		throwErrorIfConstructorArgumentIsNull(textHolder);
		throwErrorIfConstructorArgumentIsNull(metadataHolder);
		throwErrorIfConstructorArgumentIsNull(presentationHolder);
		this.textHolder = textHolder;
		this.metadataHolder = metadataHolder;
		this.presentationHolder = presentationHolder;

	}

	private void throwErrorIfConstructorArgumentIsNull(Object argument) {
		if (null == argument) {
			throw new IllegalArgumentException("Constructor argument must not be null");
		}
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

	public PresentationHolder getPresentationElements() {
		return presentationHolder;
	}
}
