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
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataRecordRelationValidatorTest {

	@Test
	public void testOneGroupNoAttributesOneTextChildWrongNameInData() {
		DataElementValidator dataElementValidator = createOneRecordRelationWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = DataGroup.withNameInData("groupDataERRORId");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid as the nameInData is invalid");
	}

	private DataElementValidator createOneRecordRelationWithNoAttributesOneTextChildReturnDataElementValidator() {
		MetadataHolder metadataHolder = createOneRecordRelationNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		return dataValidatorFactory.factor("testRecordRelationId");
	}

	private MetadataHolder createOneRecordRelationNoAttributesOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = DataCreator.createRecordRelation("test", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group, metadataHolder);
		return metadataHolder;
	}

}
