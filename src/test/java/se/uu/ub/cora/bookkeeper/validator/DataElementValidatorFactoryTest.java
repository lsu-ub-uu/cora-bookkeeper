/*
 * Copyright 2015, 2017, 2023 Uppsala University Library
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

import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataHolderPopulatorSpy;
import se.uu.ub.cora.bookkeeper.recordpart.MetadataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataElementValidatorFactoryTest {

	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();
	private MetadataHolderSpy metadataHolderDefault;
	private DataElementValidatorFactoryImp dataValidatorFactory;
	private MetadataHolderPopulatorSpy metadataHolderPopulator;
	private MetadataHolderSpy metadataHolderShouldNotBeUsed;

	@BeforeMethod
	public void setup() {
		recordTypeHolder = new HashMap<String, DataGroup>();

		metadataHolderDefault = new MetadataHolderSpy();
		metadataHolderShouldNotBeUsed = new MetadataHolderSpy();
		metadataHolderPopulator = new MetadataHolderPopulatorSpy();
		metadataHolderPopulator.MRV.setReturnValues(
				"createAndPopulateMetadataHolderFromMetadataStorage",
				List.of(metadataHolderDefault, metadataHolderShouldNotBeUsed));

		dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolderPopulator);
	}

	@Test
	public void testFactorDataValidatorMetadataGroup() {
		String elementId = "metadataGroupId";
		MetadataGroupSpy metadataGroup = new MetadataGroupSpy(elementId, "someNameInData");
		addMetadataElementSpyToMetadataHolderSpy(elementId, metadataGroup);

		DataGroupValidator validator = (DataGroupValidator) dataValidatorFactory.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);

		assertSame(validator.onlyForTestGetDataElementValidatorFactory(), dataValidatorFactory);
		assertSame(validator.onlyForTestGetMetadataHolder(), metadataHolderDefault);
		assertSame(validator.onlyForTestGetMetadataElement(), metadataGroup);

	}

	private void assertCallToMetadataHolderPopulatorAndGetMetadataElements(String elementId) {
		metadataHolderPopulator.MCR
				.assertParameters("createAndPopulateMetadataHolderFromMetadataStorage", 0);
		MetadataHolderSpy metadataHolder = (MetadataHolderSpy) metadataHolderPopulator.MCR
				.getReturnValue("createAndPopulateMetadataHolderFromMetadataStorage", 0);
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, elementId);
	}

	@Test
	public void testFactorDataValidatorMetadataTextVariable() {
		String elementId = "textVariableId";
		TextVariableSpy textVariable = new TextVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, textVariable);

		DataTextVariableValidator validator = (DataTextVariableValidator) dataValidatorFactory
				.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);

		assertSame(validator.onlyForTestGetMetadataElement(), textVariable);

	}

	@Test
	public void testFactorDataValidatorMetadataNumberVariable() {
		String elementId = "someNumberVariableId";
		NumberVariableSpy numberVariable = new NumberVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, numberVariable);

		DataNumberVariableValidator validator = (DataNumberVariableValidator) dataValidatorFactory
				.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);
		assertSame(validator.onlyForTestGetMetadataElement(), numberVariable);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariable() {
		String elementId = "collectionVariableId";
		CollectionVariableSpy collectionVariable = new CollectionVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, collectionVariable);

		DataCollectionVariableValidator validator = (DataCollectionVariableValidator) dataValidatorFactory
				.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);
		assertSame(validator.onlyForTestGetMetadataElement(), collectionVariable);
	}

	@Test
	public void testFactorDataValidatorMetadataRecordLink() {
		String elementId = "recordLinkId";
		RecordLinkSpy recordLink = new RecordLinkSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, recordLink);

		DataRecordLinkValidator validator = (DataRecordLinkValidator) dataValidatorFactory
				.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);
		assertSame(validator.onlyForTestGetMetadataElement(), recordLink);
	}

	@Test
	public void testFactorDataValidatorMetadataResourceLink() {
		String elementId = "resourceLink";

		addMetadataElementSpyToMetadataHolderSpy(elementId, new ResourceLinkSpy());

		DataResourceLinkValidator validator = (DataResourceLinkValidator) dataValidatorFactory
				.factor(elementId);

		assertCallToMetadataHolderPopulatorAndGetMetadataElements(elementId);
		assertSame(validator.onlyForTestGetMetadataHolder(), metadataHolderDefault);
	}

	private void addMetadataElementSpyToMetadataHolderSpy(String elementId,
			MetadataElement resourceLink) {
		metadataHolderDefault.elementsToReturn.put(elementId, resourceLink);
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testNotIdFound() {
		dataValidatorFactory.factor("elementNotFound");
	}

	@Test
	public void testGetMetadataHolder() {
		try {
			dataValidatorFactory.factor("elementNotFound");

			fail();
		} catch (Exception e) {
			assertSame(dataValidatorFactory.onlyForTestGetMetadataHolder(), metadataHolderDefault);
		}
	}

	@Test
	public void testGetRecordTypeHolder() {
		assertSame(dataValidatorFactory.onlyForTestGetRecordTypeHolder(), recordTypeHolder);
	}

	@Test
	public void testMakeSureOnlyOneMetadataHolderIsCreated() throws Exception {
		String elementId = "resourceLink";

		addMetadataElementSpyToMetadataHolderSpy(elementId, new ResourceLinkSpy());

		dataValidatorFactory.factor(elementId);
		var metadataHolderCall1 = dataValidatorFactory.onlyForTestGetMetadataHolder();

		dataValidatorFactory.factor(elementId);
		var metadataHolderCall2 = dataValidatorFactory.onlyForTestGetMetadataHolder();

		assertSame(metadataHolderCall1, metadataHolderCall2);
		assertSame(metadataHolderCall1, metadataHolderDefault);
		assertSame(metadataHolderCall2, metadataHolderDefault);

	}
}
