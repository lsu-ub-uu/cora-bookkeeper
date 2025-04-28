/*
 * Copyright 2015, 2017, 2023, 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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

package se.uu.ub.cora.bookkeeper.decorator;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.recordpart.MetadataGroupSpy;
import se.uu.ub.cora.bookkeeper.validator.CollectionVariableSpy;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchFactoryImp;
import se.uu.ub.cora.bookkeeper.validator.NumberVariableSpy;
import se.uu.ub.cora.bookkeeper.validator.RecordLinkSpy;
import se.uu.ub.cora.bookkeeper.validator.ResourceLinkSpy;
import se.uu.ub.cora.bookkeeper.validator.TextVariableSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataChildDecoratorFactoryTest {

	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();
	private MetadataHolderSpy metadataHolder;
	private DataChildDecoratorFactoryImp dataDecoratorFactory;

	@BeforeMethod
	public void setup() {
		metadataHolder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolder);
		dataDecoratorFactory = new DataChildDecoratorFactoryImp(recordTypeHolder);
	}

	@Test
	public void testFactorDataValidatorMetadataTextVariable() {
		String elementId = "textVariableId";
		TextVariableSpy textVariable = new TextVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, textVariable);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), textVariable);
		assertNoExtraDecoratorSetUp(decorator);
	}

	@Test
	public void testFactorDataValidatorMetadataNumberVariable() {
		String elementId = "someNumberVariableId";
		NumberVariableSpy numberVariable = new NumberVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, numberVariable);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), numberVariable);
		assertNoExtraDecoratorSetUp(decorator);
	}

	@Test
	public void testFactorDataValidatorMetadataRecordLink() {
		String elementId = "recordLinkId";
		RecordLinkSpy recordLink = new RecordLinkSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, recordLink);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), recordLink);
		assertNoExtraDecoratorSetUp(decorator);
	}

	@Test
	public void testFactorDataValidatorMetadataResourceLink() {
		String elementId = "resourceLink";
		ResourceLinkSpy resourceLink = new ResourceLinkSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, resourceLink);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), resourceLink);
		assertNoExtraDecoratorSetUp(decorator);
	}

	@Test
	public void testFactorDataValidatorMetadataGroup() {
		String elementId = "metadataGroupId";
		MetadataGroupSpy metadataGroup = new MetadataGroupSpy(elementId, "someNameInData");
		addMetadataElementSpyToMetadataHolderSpy(elementId, metadataGroup);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), metadataGroup);
		var extraDecorator = (DataGroupDecorator) decorator.onlyForTestGetExtraDecorator();
		assertSame(extraDecorator.onlyForTestGetMetadataElement(), metadataGroup);
		assertSame(extraDecorator.onlyForTestGetDataElementValidatorFactory(),
				dataDecoratorFactory);

		assertTrue(extraDecorator
				.onlyForTestGetMetadataMatchFactory() instanceof MetadataMatchFactoryImp);
	}

	private void assertNoExtraDecoratorSetUp(DataGenericDecorator decorator) {
		assertNull(decorator.onlyForTestGetExtraDecorator());
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariable() {
		String elementId = "someCollectionVariableId";
		CollectionVariableSpy collectionVariable = new CollectionVariableSpy();
		addMetadataElementSpyToMetadataHolderSpy(elementId, collectionVariable);

		var decorator = (DataGenericDecorator) dataDecoratorFactory.factor(elementId);

		assertSame(decorator.onlyForTestGetMetadataElement(), collectionVariable);
		var extraDecorator = (DataCollectionVarDecorator) decorator.onlyForTestGetExtraDecorator();
		assertSame(extraDecorator.onlyForTestGetMetadataElement(), collectionVariable);
	}

	private void addMetadataElementSpyToMetadataHolderSpy(String elementId,
			MetadataElement metadataElement) {
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> metadataElement, elementId);
	}

	@Test
	public void testGetRecordTypeHolder() {
		assertSame(dataDecoratorFactory.onlyForTestGetRecordTypeHolder(), recordTypeHolder);
	}
}
