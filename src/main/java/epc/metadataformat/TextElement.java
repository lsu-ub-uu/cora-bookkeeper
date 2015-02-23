package epc.metadataformat;


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
	private final TranslationHolder translationHolder;

	public TextElement(String id, TranslationHolder translationHolder) {
		this.id = id;
		this.translationHolder = translationHolder;
	}

	public String getId() {
		return id;
	}

	public TranslationHolder getTranslations() {
		return translationHolder;
	}

	public String getTranslationByLanguage(String language) {
		return translationHolder.getTranslation(language);
	}

}
