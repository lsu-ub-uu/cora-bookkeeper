/*
 * Copyright 2015, 2017 Uppsala University Library
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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.LimitsContainer;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.ResourceLink;
import se.uu.ub.cora.bookkeeper.metadata.StandardMetadataParameters;
import se.uu.ub.cora.bookkeeper.metadata.TextContainer;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;

public class DataValidatorFactoryTest {

	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();
	private MetadataHolder metadataHolder;
	private DataValidatorFactory dataValidatorFactory;

	@BeforeMethod
	public void setup() {
		DataGroup image = DataGroup.withNameInData("image");
		DataGroup parentId = DataGroup.withNameInData("parentId");
		image.addChild(parentId);
		parentId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		parentId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "binary"));
		recordTypeHolder.put("image", image);

		metadataHolder = new MetadataHolder();
		dataValidatorFactory = new DataValidatorFactoryImp(recordTypeHolder, metadataHolder);
	}

	@Test
	public void testFactorDataValidatorMetadataGroup() {
		metadataHolder.addMetadataElement(MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"metadataGroupId", "nameInData", "textId", "defTextId"));
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("metadataGroupId");
		assertTrue(dataGroupValidator instanceof DataGroupValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataTextVariable() {
		metadataHolder.addMetadataElement(
				TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"textVariableId", "nameInData", "textId", "defTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("textVariableId");
		assertTrue(dataGroupValidator instanceof DataTextVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataNumberVariable() {
		NumberVariable numberVariable = createNumberVariable();

		metadataHolder.addMetadataElement(numberVariable);

		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("someNumberVariableId");
		assertTrue(dataGroupValidator instanceof DataNumberVariableValidator);
	}

	private NumberVariable createNumberVariable() {
		TextContainer textContainer = TextContainer.usingTextIdAndDefTextId("someText",
				"someDefText");
		StandardMetadataParameters standardParams = StandardMetadataParameters
				.usingIdNameInDataAndTextContainer("someNumberVariableId", "someNameInData",
						textContainer);

		LimitsContainer limits = LimitsContainer.usingMinAndMax(1, 10);
		LimitsContainer warnLimits = LimitsContainer.usingMinAndMax(2, 8);

		NumberVariable numberVariable = NumberVariable
				.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(standardParams, limits,
						warnLimits, 0);
		return numberVariable;
	}

	@Test
	public void testFactorDataValidatorMetadataRecordLink() {
		metadataHolder.addMetadataElement(
				RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
						"recordLinkId", "nameInData", "textId", "defTextId", "someRecordType"));

		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("recordLinkId");
		assertTrue(dataGroupValidator instanceof DataRecordLinkValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariable() {
		metadataHolder.addMetadataElement(new CollectionVariable("collectionVariableId",
				"nameInData", "textId", "defTextId", "collectionId"));

		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("collectionVariableId");
		assertTrue(dataGroupValidator instanceof DataCollectionVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataResourceLink() {
		metadataHolder.addMetadataElement(ResourceLink.withIdAndNameInDataAndTextIdAndDefTextId(
				"masterResource", "nameInData", "textId", "defTextId"));

		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("masterResource");
		assertTrue(dataGroupValidator instanceof DataResourceLinkValidator);
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testNotIdFound() {
		dataValidatorFactory.factor("elementNotFound");
	}

}
