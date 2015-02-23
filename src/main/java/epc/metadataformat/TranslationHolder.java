package epc.metadataformat;

import java.util.HashMap;
import java.util.Map;

public class TranslationHolder {
	private Map<String, String> translations = new HashMap<>();

	public void addTranslation(String languageId, String text) {
		translations.put(languageId, text);
	}

	public String getTranslation(String languageId) {
		return translations.get(languageId);
	}

}
