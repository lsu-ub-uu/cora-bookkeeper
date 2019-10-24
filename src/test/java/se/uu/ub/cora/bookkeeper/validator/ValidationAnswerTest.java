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

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.Test;

/**
 * ValidationAnswerTest is a test class for ValidationAnswer
 * 
 * @author olov
 * 
 */
public class ValidationAnswerTest {

	@Test
	public void testInit() {
		ValidationAnswer answer = new ValidationAnswer();
		assertEquals(answer.dataIsValid(), true, "A new ValidationAnswer is valid");
		assertEquals(answer.dataIsInvalid(), false, "A new ValidationAnswer is valid");
	}

	@Test
	public void testAddErrorMessage() {
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessage("Test error");
		assertEquals(answer.dataIsValid(), false,
				"ValidationAnswer with at least one error message is not valid");
		assertEquals(answer.dataIsInvalid(), true,
				"ValidationAnswer with at least one error message is invalid");
	}

	@Test
	public void testAddErrorMessageAndError() {
		ValidationAnswer answer = new ValidationAnswer();
		Exception exception = new Exception("message");
		answer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage("Test error", exception);
		assertEquals(answer.dataIsValid(), false,
				"ValidationAnswer with at least one error message is not valid");
		assertEquals(answer.dataIsInvalid(), true,
				"ValidationAnswer with at least one error message is invalid");
		assertEquals(answer.getErrorMessages().iterator().next(), "Test error message");
	}

	@Test
	public void testAddErrorMessages() {
		Collection<String> errorMessages = new ArrayList<>();
		errorMessages.add("Test error");
		errorMessages.add("Test error2");
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessages(errorMessages);
		Collection<String> errorMessagesOut = answer.getErrorMessages();
		Iterator<String> errorMessagesOutIterator = errorMessagesOut.iterator();
		for (String errorMessage : errorMessages) {
			assertEquals(errorMessagesOutIterator.next(), errorMessage);
		}
	}

	@Test
	public void testGetErrorMessages() {
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessage("Test error");
		Collection<String> errorMessages = answer.getErrorMessages();

		assertEquals(errorMessages.iterator().next(), "Test error");
	}

	@Test
	public void testGetErrorMessagesIsNotInternalList() {
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessage("Test error");
		Collection<String> errorMessages = answer.getErrorMessages();
		int errorMessagesSize = errorMessages.size();
		errorMessages.clear();

		assertEquals(answer.getErrorMessages().size(), errorMessagesSize);
	}
}
