package se.uu.ub.cora.metadataformat.presentation;

import java.util.HashMap;
import java.util.Map;

public class PresentationHolder {
	private Map<String, PresentationElement> presentations = new HashMap<>();

	public void add(PresentationElement presentationElement) {
		presentations.put(presentationElement.getId(), presentationElement);
	}

	public PresentationElement getPresentationElement(String id) {
		return presentations.get(id);
	}

}
