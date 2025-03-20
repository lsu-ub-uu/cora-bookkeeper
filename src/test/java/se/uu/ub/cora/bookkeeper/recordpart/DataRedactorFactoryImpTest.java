/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class DataRedactorFactoryImpTest {
	private LoggerFactorySpy loggerFactory;
	private DataRedactorFactoryImp factory;
	private MetadataHolderSpy metadataHolderSpy;

	@BeforeMethod
	private void beforeMethod() {
		setUpProviders();
		metadataHolderSpy = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolderSpy);
		factory = new DataRedactorFactoryImp();
	}

	@AfterMethod
	public void afterMethod() {
		LoggerProvider.setLoggerFactory(null);
		MetadataHolderProvider.onlyForTestSetHolder(null);
	}

	private void setUpProviders() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
	}

	@Test
	public void testFactorFetchedFromProvider() {

		DataRedactorImp redactor = (DataRedactorImp) factory.factor();

		assertSame(redactor.onlyForTestGetMetadataHolder(), metadataHolderSpy);
	}

	@Test
	public void testFactorCorrectDataGroupRedactorCreatedForDependency() {
		DataRedactorImp redactor = (DataRedactorImp) factory.factor();

		assertTrue(redactor.onlyForTestGetDataGroupRedactor() instanceof DataGroupRedactorImp);
	}

	@Test
	public void testFactorCorrectMetadataMatcherFactoryCreatedForDependency() {
		DataRedactorImp redactor = (DataRedactorImp) factory.factor();

		MatcherFactoryImp matcherFactor = (MatcherFactoryImp) redactor
				.onlyForTestGetMatcherFactory();

		MetadataMatchDataImp createdMetadataMatch = (MetadataMatchDataImp) matcherFactor
				.onlyForTestGetMetadataMatchData();
		assertNotNull(createdMetadataMatch);
		MetadataHolder createdMetadataHolder = createdMetadataMatch.onlyForTestGetMetadataHolder();
		assertNotNull(createdMetadataHolder);

		assertSame(createdMetadataHolder, metadataHolderSpy);
	}

	@Test
	public void testFactorCorrectDataGroupWrapperFactoryCreatedForDependency() {
		DataRedactorImp redactor = (DataRedactorImp) factory.factor();

		assertTrue(redactor
				.onlyForTestGetDataGroupWrapperFactory() instanceof DataGroupWrapperFactoryImp);
	}
}
