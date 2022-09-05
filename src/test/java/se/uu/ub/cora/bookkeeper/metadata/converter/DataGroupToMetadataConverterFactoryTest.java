/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToMetadataConverterFactoryTest {
	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorNotMetadata() {
		DataGroup dataGroup = new DataGroupOldSpy("metadataNOT");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		converterFactory.factorForDataGroupContainingMetadata(dataGroup);
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorWrongType() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupNOT");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		converterFactory.factorForDataGroupContainingMetadata(dataGroup);
	}

	@Test
	public void testFactorGroup() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToMetadataGroupConverter);
	}

	@Test
	public void testFactorCollectionItem() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionItem");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToCollectionItemConverter);
	}

	@Test
	public void testFactorCollectionVariable() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToCollectionVariableConverter);
	}

	@Test
	public void testFactorItemCollection() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "itemCollection");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToItemCollectionConverter);
	}

	@Test
	public void testFactorTextVariable() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "textVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToTextVariableConverter);
	}

	@Test
	public void testFactorNumberVariable() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "numberVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToNumberVariableConverter);
	}

	@Test
	public void testFactorRecordLink() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordLink");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToRecordLinkConverter);
	}

	@Test
	public void testFactorResourceLink() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "resourceLink");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.forDataGroups();
		DataGroupToMetadataConverter converter = converterFactory.factorForDataGroupContainingMetadata(dataGroup);
		assertTrue(converter instanceof DataGroupToResourceLinkConverter);
	}

}
