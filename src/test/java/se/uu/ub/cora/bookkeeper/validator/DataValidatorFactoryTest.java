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

import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariableChild;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroupChild;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

import static org.testng.Assert.assertTrue;

public class DataValidatorFactoryTest {
	@Test
	public void testFactorDataValidatorMetadataGroup() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"metadataGroupId", "nameInData", "textId", "defTextId"));
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("metadataGroupId");
		assertTrue(dataGroupValidator instanceof DataGroupValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataGroupChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new MetadataGroupChild("metadataGroupChildId",
				"nameInData", "textId", "defTextId", "metadataGroupId"));
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("metadataGroupChildId");
		assertTrue(dataGroupValidator instanceof DataGroupValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataTextVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(
				TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"textVariableId", "nameInData", "textId", "defTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("textVariableId");
		assertTrue(dataGroupValidator instanceof DataTextVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataRecordLink() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(
				RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
						"recordLinkId", "nameInData", "textId", "defTextId", "someRecordType"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("recordLinkId");
		assertTrue(dataGroupValidator instanceof DataRecordLinkValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new CollectionVariable("collectionVariableId",
				"nameInData", "textId", "defTextId", "collectionId"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("collectionVariableId");
		assertTrue(dataGroupValidator instanceof DataCollectionVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariableChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new CollectionVariableChild("collectionVariableId",
				"nameInData", "textId", "defTextId", "collectionId", "scollectionId"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("collectionVariableId");
		assertTrue(dataGroupValidator instanceof DataCollectionVariableChildValidator);
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testNotIdFound() {
		MetadataHolder metadataHolder = new MetadataHolder();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		dataValidatorFactory.factor("elementNotFound");
	}

}
