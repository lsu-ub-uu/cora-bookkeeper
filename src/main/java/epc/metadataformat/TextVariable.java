package epc.metadataformat;

/**
 * TextVariable is the class that handles metadata for a RegularExpression
 * variable
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class TextVariable extends MetadataElement {

	private final String regularExpression;

	public TextVariable(String id, String dataId, String textId,
			String deffTextId, String regularExpression) {
		super(id, dataId, textId, deffTextId);
		this.regularExpression = regularExpression;
	}

	public String getRegularExpression() {
		return regularExpression;
	}

}
