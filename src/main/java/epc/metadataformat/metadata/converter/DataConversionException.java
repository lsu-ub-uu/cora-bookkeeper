package epc.metadataformat.metadata.converter;

public final class DataConversionException extends RuntimeException {

	private static final long serialVersionUID = 7242634871228048097L;

	public static DataConversionException withMessage(String message) {
		return new DataConversionException(message);
	}

	public static DataConversionException withMessageAndException(String message,
			Exception exception) {
		return new DataConversionException(message, exception);
	}

	private DataConversionException(String message) {
		super(message);
	}

	private DataConversionException(String message, Exception exception) {
		super(message, exception);
	}

}
