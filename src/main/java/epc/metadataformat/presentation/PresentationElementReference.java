package epc.metadataformat.presentation;

public class PresentationElementReference implements PresentationChildReference {

	private String elementRef;
	private String presentationOf;
	private String elementRefMinimized;

	public PresentationElementReference(String elementRef, String presentationOf) {
		this.elementRef = elementRef;
		this.presentationOf = presentationOf;
	}

	public String getElementRef() {
		return elementRef;
	}

	public String getPresentationOf() {
		return presentationOf;
	}

	public void setElementRefMinimized(String elementRefMinimized) {
		this.elementRefMinimized = elementRefMinimized;
	}

	public String getElementRefMinimized() {
		return elementRefMinimized;
	}

	@Override
	public String getReferenceId() {
		return getElementRef();
	}

}
