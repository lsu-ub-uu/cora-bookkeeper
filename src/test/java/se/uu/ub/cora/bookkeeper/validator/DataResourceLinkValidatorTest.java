/*
 * Copyright 2015, 2019, 2023 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderImp;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;

public class DataResourceLinkValidatorTest {
	private static final String EMPTY_NAME_IN_DATA = "";
	private static final String SOME_NAME_IN_DATA = "someNameInData";
	private DataResourceLinkValidator dataResourceLinkValidator;
	private MetadataHolder metadataHolder = new MetadataHolderImp();
	private DataResourceLinkSpy dataResourceLink;

	@BeforeMethod
	public void setUp() {
		dataResourceLink = new DataResourceLinkSpy();
		dataResourceLinkValidator = new DataResourceLinkValidator(metadataHolder);
	}

	@Test
	public void testValidate() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData",
				() -> SOME_NAME_IN_DATA);

		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);

		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		dataResourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData",
				() -> EMPTY_NAME_IN_DATA);

		validateAndAssertDataIsInvalid(dataResourceLink);
	}

	private void validateAndAssertDataIsInvalid(DataResourceLink dataResourceLink) {
		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

}
