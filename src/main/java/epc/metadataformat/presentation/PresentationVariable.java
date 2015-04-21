package epc.metadataformat.presentation;

public class PresentationVariable implements PresentationElement {

	private String id;
	private String refVarId;
	private Mode mode;

	public PresentationVariable(String id, String refVarId, Mode input) {
		this.id = id;
		this.refVarId = refVarId;
		this.mode = input;
	}

	public String getId() {
		return id;
	}

	public String getRefVarId() {
		return refVarId;
	}

	public Mode getMode() {
		return mode;
	}

	public static enum Mode {
		INPUT("input"), OUTPUT("output");
		private String value;

		Mode(final String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
