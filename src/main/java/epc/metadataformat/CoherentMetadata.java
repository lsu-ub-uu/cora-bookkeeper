package epc.metadataformat;

import java.util.Map;

/**
 * CoherentMetadata is a class that works as a container arround all metadata in
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

	private final Map<String, TextElement> textElements;
	private final Map<String, MetadataElement> metadataElements;

	public CoherentMetadata(Map<String, TextElement> textElements,
			Map<String, MetadataElement> metadataElements) {
		this.textElements = textElements;
		this.metadataElements = metadataElements;
	}

	/**
	 * texts metadataElements collections presentations
	 * 
	 */

	public Map<String, MetadataElement> getMetadataElements() {
		return metadataElements;
	}

	public Map<String, TextElement> getTextElements() {
		return textElements;
	}

	public String getTextTranslation(String languageId, String textId) {
		return textElements.get(textId).getTranslationByLanguage(languageId);
	}

}
