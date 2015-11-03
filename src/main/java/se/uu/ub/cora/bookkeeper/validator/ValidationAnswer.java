/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.bookkeeper.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ValidationAnswer is a class to deliver a validation answer, it contains a
 * boolean with the validation status and a list with errors if the data was not
 * valid
 * 
 * @author olov
 * 
 */
public class ValidationAnswer {

	private List<String> errorMessages = new ArrayList<>();

	/**
	 * addErrorMessage, adds the entered errorMessage to the internal list of
	 * error messages
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
		return copyErrorMessages();
	}

	private List<String> copyErrorMessages() {
		List<String> errorMessagesOut = new ArrayList<>();
		for (String message : errorMessages) {
			errorMessagesOut.add(message);
		}
		return errorMessagesOut;
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
