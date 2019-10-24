package se.uu.ub.cora.bookkeeper.metadata;

public class StandardMetadataParameters {

	public String id;
	public String nameInData;
	public String textId;
	public String defTextId;

	private StandardMetadataParameters(String id, String nameInData, TextContainer textContainer) {
		this.id = id;
		this.nameInData = nameInData;
		possiblySetTexts(textContainer);
	}

	private final void possiblySetTexts(TextContainer textContainer) {
		if (textContainer != null) {
			textId = textContainer.textId;
			defTextId = textContainer.defTextId;
		}
	}

	public static StandardMetadataParameters usingIdNameInDataAndTextContainer(String id,
			String nameInData, TextContainer textContainer) {
		return new StandardMetadataParameters(id, nameInData, textContainer);
	}

}
