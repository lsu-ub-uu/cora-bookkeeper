package se.uu.ub.cora.metadataformat;

import java.util.HashMap;
import java.util.Map;

public class TextHolder {
	
	private Map<String, TextElement>textElements = new HashMap<>();
	
	public void addTextElement(TextElement textElement) {
		textElements.put(textElement.getId(), textElement);
	}

	public TextElement getTextElement(String textId) {
		return textElements.get(textId);
	}
	
}
