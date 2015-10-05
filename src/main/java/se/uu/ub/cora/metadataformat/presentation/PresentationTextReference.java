package se.uu.ub.cora.metadataformat.presentation;

public class PresentationTextReference implements PresentationChildReference {

	private String textRef;

	public PresentationTextReference(String textRef) {
		this.textRef = textRef;
	}

	public String getTextRef() {
		return textRef;
	}

	@Override
	public String getReferenceId() {
		return textRef;
	}

}
