/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderPopulator;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderPopulatorImp;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataHolderPopulatorSpy;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;

public class DataRedactorFactoryImpTest {
	private DataRedactorFactoryOverrideForTest factoryForTest;
	MetadataHolderPopulatorSpy metadataHolderPopulator;

	@BeforeMethod
	private void beforeMethod() {
		metadataHolderPopulator = new MetadataHolderPopulatorSpy();
		factoryForTest = new DataRedactorFactoryOverrideForTest();
	}

	@Test
	public void testFactorCorrectMetadataHolderPopulatorCreatedByDefault() throws Exception {
		DataRedactorFactoryOverrideForTest forTest = new DataRedactorFactoryOverrideForTest();

		assertTrue(forTest
				.useOriginalCreateMetadataHolderPopulator() instanceof MetadataHolderPopulatorImp);
	}

	@Test
	public void testFactorCorrectMetadataHolderCreatedFromPopulator() throws Exception {
		DataRedactorImp redactor = (DataRedactorImp) factoryForTest.factor();

		var metadataHolder = metadataHolderPopulator.MCR
				.getReturnValue("createAndPopulateMetadataHolderFromMetadataStorage", 0);

		assertSame(redactor.onlyForTestGetMetadataHolder(), metadataHolder);
	}

	@Test
	public void testFactorCorrectDataGroupRedactorCreatedForDependency() throws Exception {
		DataRedactorImp redactor = (DataRedactorImp) factoryForTest.factor();

		assertTrue(redactor.onlyForTestGetDataGroupRedactor() instanceof DataGroupRedactorImp);
	}

	@Test
	public void testFactorCorrectMetadataMatcherFactoryCreatedForDependency() throws Exception {
		DataRedactorImp redactor = (DataRedactorImp) factoryForTest.factor();

		MatcherFactoryImp matcherFactor = (MatcherFactoryImp) redactor
				.onlyForTestGetMatcherFactory();

		MetadataMatchDataImp createdMetadataMatch = (MetadataMatchDataImp) matcherFactor
				.onlyForTestGetMetadataMatchData();
		assertNotNull(createdMetadataMatch);
		MetadataHolder createdMetadataHolder = createdMetadataMatch.onlyForTestGetMetadataHolder();
		assertNotNull(createdMetadataHolder);

		var metadataHolder = metadataHolderPopulator.MCR
				.getReturnValue("createAndPopulateMetadataHolderFromMetadataStorage", 0);
		assertSame(createdMetadataHolder, metadataHolder);
	}

	@Test
	public void testFactorCorrectDataGroupWrapperFactoryCreatedForDependency() throws Exception {
		DataRedactorImp redactor = (DataRedactorImp) factoryForTest.factor();

		assertTrue(redactor
				.onlyForTestGetDataGroupWrapperFactory() instanceof DataGroupWrapperFactoryImp);
	}

	private class DataRedactorFactoryOverrideForTest extends DataRedactorFactoryImp {

		MetadataHolderPopulator useOriginalCreateMetadataHolderPopulator() {
			return super.createMetadataHolderPopulator();
		}

		@Override
		MetadataHolderPopulator createMetadataHolderPopulator() {
			return metadataHolderPopulator;
		}
	}
}
