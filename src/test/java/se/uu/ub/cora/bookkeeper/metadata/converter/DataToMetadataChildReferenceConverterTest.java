/*
 * Copyright 2015, 2017, 2019, 2020 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public class DataToMetadataChildReferenceConverterTest {
	private DataGroupSpy dataGroupChildReference;

	@BeforeMethod
	public void beforeMethod() {
		dataGroupChildReference = createChildRefOtherMetadata();
	}

	@Test
	public void testToMetadata() {
		DataToMetadataChildReferenceConverter converter = DataToMetadataChildReferenceConverter
				.fromDataGroup(dataGroupChildReference);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadata");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMax(), 16);
	}

	private DataGroupSpy createChildRefOtherMetadata() {
		return DataToMetadataElementTestHelper.createChildReference("otherMetadata", "0", "16");
	}

	@Test
	public void testToMetadataRepeatMaxValueX() {
		DataGroup dataGroup = DataToMetadataElementTestHelper.createChildReference("otherMetadata",
				"0", "X");

		DataToMetadataChildReferenceConverter converter = DataToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadata");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE);
	}

	@Test
	public void testToMetadataWithThreeCollectTermsIndexStorageAndPermission() {
		setNumOfChildRefCollectTermsToReturnFromSpy(dataGroupChildReference, "index", "permission",
				"storage");

		DataToMetadataChildReferenceConverter converter = DataToMetadataChildReferenceConverter
				.fromDataGroup(dataGroupChildReference);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 3);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "someCollectTerm0");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "index");

		assertEquals(metadataChildReference.getCollectTerms().get(1).id, "someCollectTerm1");
		assertEquals(metadataChildReference.getCollectTerms().get(1).type, "permission");

		assertEquals(metadataChildReference.getCollectTerms().get(2).id, "someCollectTerm2");
		assertEquals(metadataChildReference.getCollectTerms().get(2).type, "storage");
	}

	@Test
	public void testToMetadataRecordPartConstraintReadWrite() {
		DataToMetadataElementTestHelper.addAtomic(dataGroupChildReference, "recordPartConstraint",
				"readWrite");

		DataToMetadataChildReferenceConverter converter = DataToMetadataChildReferenceConverter
				.fromDataGroup(dataGroupChildReference);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getRecordPartConstraint(), ConstraintType.READ_WRITE);
	}

	private void setNumOfChildRefCollectTermsToReturnFromSpy(DataGroupSpy childReference,
			String... typeOfCollectTerms) {
		childReference.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "childRefCollectTerm");

		List<DataRecordLink> links = createLinksWithAttributes(typeOfCollectTerms);
		childReference.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName", () -> links,
				DataRecordLink.class, "childRefCollectTerm");
	}

	private List<DataRecordLink> createLinksWithAttributes(String... typeOfCollectTerms) {
		List<DataRecordLink> links = new ArrayList<>();
		int counter = 0;
		for (String typeOfCollectTerm : typeOfCollectTerms) {
			DataRecordLinkSpy link = createLinkWithAttribute(typeOfCollectTerm, counter++);
			links.add(link);
		}
		return links;
	}

	private DataRecordLinkSpy createLinkWithAttribute(String typeOfCollectTerm, int counter) {
		DataRecordLinkSpy link = DataToMetadataElementTestHelper.createLink("collectTerm",
				"someCollectTerm" + counter);
		addAttributeToLink(link, "type", typeOfCollectTerm);
		return link;
	}

	private void addAttributeToLink(DataRecordLinkSpy link, String name, String value) {
		DataAttributeSpy dataAttributeSpy = new DataAttributeSpy();
		dataAttributeSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		link.MRV.setSpecificReturnValuesSupplier("getAttribute", () -> dataAttributeSpy, name);
	}
}
