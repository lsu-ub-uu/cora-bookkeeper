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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataConversionExceptionTest {
	@Test
	public void testInitWithMessage() {
		String message = "message";
		DataConversionException dataConversionException = DataConversionException
				.withMessage(message);
		assertEquals(dataConversionException.getMessage(), "message");
	}

	@Test
	public void testInitWithMessageAndExcepiton() {
		String message = "message";
		Exception e = new Exception();
		DataConversionException dataConversionException = DataConversionException
				.withMessageAndException(message, e);
		assertEquals(dataConversionException.getMessage(), "message");
	}
}
