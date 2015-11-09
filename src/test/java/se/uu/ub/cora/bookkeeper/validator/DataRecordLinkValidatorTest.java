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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DataRecordLinkValidatorTest {
	private RecordLink dataLink;
	private DataRecordLinkValidator dataLinkValidator;

	@BeforeMethod
	public void setUp() {
		dataLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id",
				"nameInData", "textId", "defTextId", "linkedRecordType");
		dataLinkValidator = new DataRecordLinkValidator(dataLink);
	}

	@Test
	public void testValidate() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordType() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "notMyRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("",
				"linkedRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData", "", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 2);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData", "linkedRecordType", "");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedMissingRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedEmptyRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatIdShouldNotExist() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedLinkedPath() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedLinkedPathShouldNeverExist() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");
		dataRecordLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}
}
