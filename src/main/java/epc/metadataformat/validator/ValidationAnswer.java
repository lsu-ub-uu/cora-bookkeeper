package epc.metadataformat.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ValidationAnswer is a class to deliver a validation answer, it contains a boolean with the
 * validation status and a list with errors if the data was not valid
 * 
 * @author olov
 * 
 */
public class ValidationAnswer {

	private List<String> errorMessages = new ArrayList<>();

	/**
	 * addErrorMessage, adds the entered errorMessage to the internal list of error messages
	 * 
	 * @param errorMessage
	 *            A String with an errorMessage to add to the internal list
	 */
	public void addErrorMessage(String errorMessage) {
		errorMessages.add(errorMessage);
	}

	/**
	 * dataIsValid returns true if the validated data is found to be valid
	 * 
	 * @return A boolean true for valid data
	 */
	public boolean dataIsValid() {
		return isValid();
	}

	/**
	 * dataIsInvalid returns true if the validated data is found to be NOT valid
	 * 
	 * @return A boolean true for invalid data
	 */
	public boolean dataIsInvalid() {
		return !isValid();
	}

	private boolean isValid() {
		return errorMessages.isEmpty();
	}

	public Collection<String> getErrorMessages() {
		return errorMessages;
	}

	public void addErrorMessages(Collection<String> errorMessagesIn) {
		for (String errorMessage : errorMessagesIn) {
			errorMessages.add(errorMessage);
		}

	}

	public void addErrorMessageAndAppendErrorMessageFromExceptionToMessage(String message,
			Exception exception) {
		addErrorMessage(message + " " + exception.getMessage());
	}
}
