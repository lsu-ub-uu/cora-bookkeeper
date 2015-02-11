package epc.metadataformat;

import java.util.Map;

/**
 * TextElement holds information about a text and the translation of that text
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class TextElement {

	private final String id;
	private final Map<String, String> translations;

	public TextElement(String id, Map<String, String> translations) {
		this.id = id;
		this.translations = translations;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getTranslations() {
		return translations;
	}

	public String getTranslationByLanguage(String language) {
		return translations.get(language);
	}

}
