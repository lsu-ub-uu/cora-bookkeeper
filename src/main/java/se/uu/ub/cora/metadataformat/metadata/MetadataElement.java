package se.uu.ub.cora.metadataformat.metadata;

/**
 * MetadataElement is an abstract class that holds the common attributes assosiated with
 * metadataElements
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public abstract class MetadataElement {

	private final String id;
	private final String nameInData;
	private final String textId;
	private final String defTextId;

	protected MetadataElement(String id, String nameInData, String textId, String defTextId) {
		this.id = id;
		this.nameInData = nameInData;
		this.textId = textId;
		this.defTextId = defTextId;
	}

	public String getId() {
		return id;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getTextId() {
		return textId;
	}

	public String getDefTextId() {
		return defTextId;
	}
}
