package epc.metadataformat.validator;

public final class DataValidationException extends RuntimeException {

	public static DataValidationException withMessage(String message) {
		return new DataValidationException(message);
	}

	private DataValidationException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -6527616505506297547L;

}
