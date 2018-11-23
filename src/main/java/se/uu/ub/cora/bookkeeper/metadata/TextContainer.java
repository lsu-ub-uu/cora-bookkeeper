package se.uu.ub.cora.bookkeeper.metadata;

public class TextContainer {
	public String textId;
	public String defTextId;

	public TextContainer(String textId, String defTextId) {
		this.textId = textId;
		this.defTextId = defTextId;
	}

	public static TextContainer usingTextIdAndDefTextId(String textId, String defTextId) {
		return new TextContainer(textId, defTextId);
	}

}
