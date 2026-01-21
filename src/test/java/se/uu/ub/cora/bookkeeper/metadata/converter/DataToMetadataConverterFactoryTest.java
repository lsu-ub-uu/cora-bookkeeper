/*
 * Copyright 2015, 2019, 2026 Uppsala University Library
 * Copyright 2016, 2025 Olov McKie
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToMetadataConverterFactoryTest {
	private static final String METADATA = "metadata";
	private DataToMetadataConverterFactory converterFactory;

	@BeforeMethod
	public void beforeMethod() {
		converterFactory = DataToMetadataConverterFactoryImp.forDataGroups();
	}

	@Test(expectedExceptions = DataConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "No converter found for DataRecordGroup with nameInData: metadataNOT")
	public void testFactorNotMetadata() {
		DataRecordGroupSpy dataRecordGroup1 = new DataRecordGroupSpy();
		dataRecordGroup1.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "metadataNOT");
		dataRecordGroup1.MRV.setDefaultReturnValuesSupplier("getType", () -> "group");
		DataRecordGroupSpy dataRecordGroup = dataRecordGroup1;

		converterFactory.factorForDataContainingMetadata(dataRecordGroup);
	}

	@Test(expectedExceptions = DataConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "No converter found for DataRecordGroup with type: groupNOT")
	public void testFactorWrongType() {
		createConverterForType("groupNOT");
	}

	private DataToMetadataConverter createConverterForType(String type) {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> METADATA);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(type), "type");
		return converterFactory.factorForDataContainingMetadata(dataRecordGroup);
	}

	@Test
	public void testFactorGroup() {
		DataToMetadataConverter converter = createConverterForType("group");

		assertTrue(converter instanceof DataToMetadataGroupConverter);
	}

	@Test
	public void testFactorCollectionItem() {
		DataToMetadataConverter converter = createConverterForType("collectionItem");

		assertTrue(converter instanceof DataToCollectionItemConverter);
	}

	@Test
	public void testFactorCollectionVariable() {
		DataToMetadataConverter converter = createConverterForType("collectionVariable");

		assertTrue(converter instanceof DataToCollectionVariableConverter);
	}

	@Test
	public void testFactorItemCollection() {
		DataToMetadataConverter converter = createConverterForType("itemCollection");

		assertTrue(converter instanceof DataToItemCollectionConverter);
	}

	@Test
	public void testFactorTextVariable() {
		DataToMetadataConverter converter = createConverterForType("textVariable");

		assertTrue(converter instanceof DataToTextVariableConverter);
	}

	@Test
	public void testFactorNumberVariable() {
		DataToMetadataConverter converter = createConverterForType("numberVariable");

		assertTrue(converter instanceof DataToNumberVariableConverter);
	}

	@Test
	public void testFactorRecordLink() {
		DataToMetadataConverter converter = createConverterForType("recordLink");

		assertTrue(converter instanceof DataToRecordLinkConverter);
	}

	@Test
	public void testFactorResourceLink() {
		DataToMetadataConverter converter = createConverterForType("resourceLink");

		assertTrue(converter instanceof DataToResourceLinkConverter);
	}

}
