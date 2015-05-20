package epc.metadataformat.metadata;

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
	private final String dataId;
	private final String textId;
	private final String defTextId;

	protected MetadataElement(String id, String dataId, String textId, String defTextId) {
		this.id = id;
		this.dataId = dataId;
		this.textId = textId;
		this.defTextId = defTextId;
	}

	public String getId() {
		return id;
	}

	public String getDataId() {
		return dataId;
	}

	public String getTextId() {
		return textId;
	}

	public String getDefTextId() {
		return defTextId;
	}
}
