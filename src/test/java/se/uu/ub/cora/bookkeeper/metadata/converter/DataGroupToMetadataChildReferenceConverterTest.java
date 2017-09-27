/*
 * Copyright 2015, 2017 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;

public class DataGroupToMetadataChildReferenceConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createChildRefOtherMetadata();

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey", "SOME_KEY"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secretKey", "SECRET_KEY"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey", "READONLY_KEY"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");
	}

	private DataGroup createChildRefOtherMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");

		DataGroup ref = DataGroup.withNameInData("ref");
		ref.addAttributeByIdWithValue("type", "group");
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "otherMetadata"));
		dataGroup.addChild(ref);
		return dataGroup;
	}

	@Test
	public void testToMetadataFalse() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "false"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), false);
		assertEquals(metadataChildReference.getSecretKey(), "");
		assertEquals(metadataChildReference.isReadOnly(), false);
		assertEquals(metadataChildReference.getReadOnlyKey(), "");
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
		assertEquals(metadataChildReference.getRepeatMinKey(), "");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), false);
		assertEquals(metadataChildReference.getSecretKey(), "");
		assertEquals(metadataChildReference.isReadOnly(), false);
		assertEquals(metadataChildReference.getReadOnlyKey(), "");
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testToMetadataNotBooleanValue() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "NOT_BOOLEAN_VALUE"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testToMetadataNotBooleanValueReadOnly() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();

	}

	@Test
	public void testToMetadataRepeatMaxValueX() {
		DataGroup dataGroup = createChildRefOtherMetadata();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "X"));

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

		DataGroup childRefCollectIndexTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectIndexTerm", "titleCollectIndexTerm", "index");
		dataGroup.addChild(childRefCollectIndexTerm);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 1);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "titleCollectIndexTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "index");
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = createChildRefOtherMetadata();

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		return dataGroup;
	}

	@Test
	public void testToMetadataWithTwoCollectIndexTerms() {
		DataGroup dataGroup = createDataGroup();

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
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "titleCollectIndexTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "index");
		assertEquals(metadataChildReference.getCollectTerms().get(1).id,
				"freeTextCollectIndexTerm");
	}

	private DataGroup createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
			String linkedRecordType, String indexTermId, String type) {
		DataGroup childRefTerm = DataGroup.withNameInData("childRefCollectTerm");
		childRefTerm
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		childRefTerm.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", indexTermId));
		childRefTerm.addAttributeByIdWithValue("type", type);
		return childRefTerm;
	}

	@Test
	public void testToMetadataWithCollectPermissionTerm() {
		DataGroup dataGroup = createDataGroup();

		DataGroup childRefCollectPermissionTerm = createChildRefCollectTermWithLinkedRecordTypeAndIdAndType(
				"collectPermissionTerm", "titleCollectPermissionTerm", "permission");
		dataGroup.addChild(childRefCollectPermissionTerm);

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getCollectTerms().size(), 1);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id,
				"titleCollectPermissionTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "permission");
	}

	@Test
	public void testToMetadataWithCollectIndexTermAndCollectPermissionTerm() {
		DataGroup dataGroup = createDataGroup();

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
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "titleCollectIndexTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "index");

		assertEquals(metadataChildReference.getCollectTerms().get(1).id,
				"titleCollectPermissionTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(1).type, "permission");
	}
}
