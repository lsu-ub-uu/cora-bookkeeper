/*
 * Copyright 2015 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataResourceLinkValidatorTest {
	private DataResourceLinkValidator dataResourceLinkValidator;
	private MetadataHolder metadataHolder = new MetadataHolder();

	@BeforeMethod
	public void setUp() {
		TextVariable streamIdTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("streamIdTextVar",
						"streamId", "streamIdTextVarText", "streamIdTextVarDefText",
						"(^[0-9A-Za-z:-_]{2,50}$)");

		metadataHolder.addMetadataElement(streamIdTextVar);

		dataResourceLinkValidator = new DataResourceLinkValidator(metadataHolder);
	}

	@Test
	public void testValidate() {
		DataGroup dataResourceLink = DataCreator.createResourceLinkGroupWithNameInDataAndStreamId(
				"nameInData", "imageBinary:123456");
		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateInvalidStreamId() {
		DataGroup dataResourceLink = DataCreator.createResourceLinkGroupWithNameInDataAndStreamId(
				"nameInData", "imageBinary:123456ÖÅÖ");
		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataGroup dataResourceLink = DataCreator
				.createResourceLinkGroupWithNameInDataAndStreamId("", "imageBinary:123456ÖÅÖ");
		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyStreamId() {
		DataGroup dataResourceLink = DataCreator
				.createResourceLinkGroupWithNameInDataAndStreamId("nameInData", "");
		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoStreamId() {
		DataGroup dataResourceLink = DataGroup.withNameInData("nameInData");

		ValidationAnswer validationAnswer = dataResourceLinkValidator
				.validateData(dataResourceLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}
}
