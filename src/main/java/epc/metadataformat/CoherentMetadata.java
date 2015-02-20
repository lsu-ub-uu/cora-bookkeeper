package epc.metadataformat;

import java.util.HashMap;
import java.util.Map;

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

	private Map<String, TextElement> textElements = new HashMap<>();
	private Map<String, MetadataElement> metadataElements = new HashMap<>();

	/**
	 * This empty constructor uses the default maps to store texts and metadata
	 */
	public CoherentMetadata() {
		//This constructor makes it possible to uses the default maps 
	}
	
	public CoherentMetadata(Map<String, TextElement> textElements,
			Map<String, MetadataElement> metadataElements) {
		if (null == textElements) {
			throw new IllegalArgumentException("textElements must not be null");
		}
		if (null == metadataElements) {
			throw new IllegalArgumentException(
					"metadataElements must not be null");
		}
		this.textElements = textElements;
		this.metadataElements = metadataElements;

	}

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
