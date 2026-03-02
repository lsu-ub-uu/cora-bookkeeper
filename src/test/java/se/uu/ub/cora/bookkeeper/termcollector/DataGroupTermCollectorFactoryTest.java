/*
 * Copyright 2026 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.termcollector;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.termcollector.internal.DataGroupTermCollectorImp;
import se.uu.ub.cora.bookkeeper.validator.DataFilterCreatorImp;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class DataGroupTermCollectorFactoryTest {

	private DataGroupTermCollectorFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		factory = new DataGroupTermCollectorFactoryImp();
	}

	@Test
	public void testName() {
		MetadataHolderSpy metadataHolder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolder);

		DataGroupTermCollectorImp collector = (DataGroupTermCollectorImp) factory.factor();
		DataFilterCreatorImp filterCreator = (DataFilterCreatorImp) collector
				.onlyForTestGetDataFilterCreator();

		assertEquals(filterCreator.onlyForTestGetMetadataHolder(), metadataHolder);
	}
}
