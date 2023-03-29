/*
 * Copyright 2021 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataValidatorFactoryTest {
	private Map<String, DataGroup> recordTypeHolderSpy = new HashMap<>();
	private MetadataHolderSpy metadataHolderSpy;

	@Test
	public void testFactor() throws Exception {
		metadataHolderSpy = new MetadataHolderSpy();

		DataValidatorFactory factory = new DataValidatorFactoryImp();

		DataValidatorImp dataValidator = (DataValidatorImp) factory.factor(recordTypeHolderSpy,
				metadataHolderSpy);
		assertTrue(dataValidator instanceof DataValidatorImp);

		assertSame(dataValidator.getRecordTypeHolder(), recordTypeHolderSpy);
		DataElementValidatorFactoryImp dataElementValidatorFactory = (DataElementValidatorFactoryImp) dataValidator
				.getDataElementValidatorFactory();
		assertTrue(dataElementValidatorFactory instanceof DataElementValidatorFactoryImp);
		assertSame(dataElementValidatorFactory.getRecordTypeHolder(), recordTypeHolderSpy);
		assertSame(dataElementValidatorFactory.getMetadataHolder(), metadataHolderSpy);
	}
}
