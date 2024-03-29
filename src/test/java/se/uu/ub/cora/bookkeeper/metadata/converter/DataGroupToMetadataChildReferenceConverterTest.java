/*
 * Copyright 2015, 2017, 2019, 2020 Uppsala University Library
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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToMetadataChildReferenceConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createChildRefOtherMetadata();

		dataGroup.addChild(new DataAtomicOldSpy("repeatMin", "0"));
		dataGroup.addChild(new DataAtomicOldSpy("repeatMax", "16"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMax(), 16);
	}

	private DataGroup createChildRefOtherMetadata() {
		DataGroup dataGroup = new DataGroupOldSpy("childReference");

		DataGroup ref = new DataGroupOldSpy("ref");
		ref.addAttributeByIdWithValue("type", "group");
		ref.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataGroup"));
		ref.addChild(new DataAtomicOldSpy("linkedRecordId", "otherMetadata"));
		dataGroup.addChild(ref);
		return dataGroup;
	}

	@Test
	public void testToMetadataNoNonMandatoryInfo() {
		DataGroup dataGroup = createDataGroup();

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMax(), 16);
	}

	@Test
	public void testToMetadataRepeatMaxValueX() {
		DataGroup dataGroup = createChildRefOtherMetadata();
		dataGroup.addChild(new DataAtomicOldSpy("repeatMin", "0"));
		dataGroup.addChild(new DataAtomicOldSpy("repeatMax", "X"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE);
	}

	@Test
	public void testToMetadataWithOneCollectIndexTerm() {
		DataGroup dataGroup = createDataGroup();
		setNumOfChildRefCollectTermsToReturnFromSpy(dataGroup, 1);

		DataGroup childRefCollectIndexTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectIndexTerm", "titleCollectIndexTerm", "index");
		dataGroup.addChild(childRefCollectIndexTerm);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 1);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id,
				"someLinkedRecordIdFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type,
				"someAttributeTypeFromSpy");
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = createChildRefOtherMetadata();

		dataGroup.addChild(new DataAtomicOldSpy("repeatMin", "0"));
		dataGroup.addChild(new DataAtomicOldSpy("repeatMax", "16"));
		return dataGroup;
	}

	@Test
	public void testToMetadataWithTwoCollectIndexTerms() {
		DataGroup dataGroup = createDataGroup();
		setNumOfChildRefCollectTermsToReturnFromSpy(dataGroup, 2);

		DataGroup childRefCollectIndexTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectIndexTerm", "titleCollectIndexTerm", "index");
		childRefCollectIndexTerm.setRepeatId("0");
		dataGroup.addChild(childRefCollectIndexTerm);

		DataGroup childRefCollectIndexTerm2 = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectIndexTerm", "freeTextCollectIndexTerm", "index");
		childRefCollectIndexTerm2.setRepeatId("1");
		dataGroup.addChild(childRefCollectIndexTerm2);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 2);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id,
				"someLinkedRecordIdFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type,
				"someAttributeTypeFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(1).id,
				"someLinkedRecordIdFromSpy");
	}

	private DataGroup createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
			String linkedRecordType, String indexTermId, String type) {
		DataGroup childRefTerm = new DataGroupOldSpy("childRefCollectTerm");
		childRefTerm.addChild(new DataAtomicOldSpy("linkedRecordType", linkedRecordType));
		childRefTerm.addChild(new DataAtomicOldSpy("linkedRecordId", indexTermId));
		childRefTerm.addAttributeByIdWithValue("type", type);
		return childRefTerm;
	}

	@Test
	public void testToMetadataWithCollectPermissionTerm() {
		DataGroup dataGroup = createDataGroup();
		setNumOfChildRefCollectTermsToReturnFromSpy(dataGroup, 1);

		DataGroup childRefCollectPermissionTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectPermissionTerm", "titleCollectPermissionTerm", "permission");
		dataGroup.addChild(childRefCollectPermissionTerm);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 1);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id,
				"someLinkedRecordIdFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type,
				"someAttributeTypeFromSpy");
	}

	@Test
	public void testToMetadataWithCollectIndexTermAndCollectPermissionTerm() {
		DataGroup dataGroup = createDataGroup();

		setNumOfChildRefCollectTermsToReturnFromSpy(dataGroup, 2);

		DataGroup childRefCollectIndexTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectIndexTerm", "titleCollectIndexTerm", "index");
		dataGroup.addChild(childRefCollectIndexTerm);

		DataGroup childRefCollectPermissionTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectPermissionTerm", "titleCollectPermissionTerm", "permission");
		dataGroup.addChild(childRefCollectPermissionTerm);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 2);

		assertEquals(metadataChildReference.getCollectTerms().get(0).id,
				"someLinkedRecordIdFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type,
				"someAttributeTypeFromSpy");

		assertEquals(metadataChildReference.getCollectTerms().get(1).id,
				"someLinkedRecordIdFromSpy");
		assertEquals(metadataChildReference.getCollectTerms().get(1).type,
				"someAttributeTypeFromSpy");
	}

	private void setNumOfChildRefCollectTermsToReturnFromSpy(DataGroup dataGroup,
			int numOfCollectTermsToReturn) {
		((DataGroupOldSpy) dataGroup).numOfGetAllGroupsWithNameInDataToReturn
				.put("childRefCollectTerm", numOfCollectTermsToReturn);
	}

	@Test
	public void testToMetadataRecordPartConstraintReadWrite() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(new DataAtomicOldSpy("recordPartConstraint", "readWrite"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getRecordPartConstraint(), ConstraintType.READ_WRITE);
	}

	@Test
	public void testToMetadataRecordPartConstraintWrite() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(new DataAtomicOldSpy("recordPartConstraint", "write"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getRecordPartConstraint(), ConstraintType.WRITE);
	}
}
