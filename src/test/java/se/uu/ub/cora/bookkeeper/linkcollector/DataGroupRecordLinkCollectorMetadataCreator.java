package se.uu.ub.cora.bookkeeper.linkcollector;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderImp;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupRecordLinkCollectorMetadataCreator {
	private MetadataHolder metadataHolder = new MetadataHolderImp();

	void addMetadataForOneGroupWithNoLink(String id) {
		metadataHolder
				.addMetadataElement(addMetadataForOneGroupWithNoLinkUsingIdAndNameInData(id, id));
	}

	private MetadataGroup addMetadataForOneGroupWithNoLinkUsingIdAndNameInData(String id,
			String nameInData) {
		return MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id + "Group",
				nameInData + "Group", id + "GroupTextId", id + "GroupDefTextId");
	}

	void addMetadataForOneGroupWithOneLink(String id) {
		addMetadataForOneGroupWithNoLink(id);

		RecordLink recordLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(id + "Link",
						id + "Link", id + "LinkTextId", id + "LinkDefTextId", "linkedRecordType");
		metadataHolder.addMetadataElement(recordLink);

		addChildReferenceParentIdChildRecordTypeIdMinMax(id + "Group", "metadataGroup", id + "Link",
				1, 15);
	}

	void addChildReferenceParentIdChildRecordTypeIdMinMax(String from, String linkedRecordType,
			String to, int min, int max) {
		MetadataGroup topGroup = (MetadataGroup) metadataHolder.getMetadataElement(from);

		MetadataChildReference reference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(linkedRecordType, to,
						min, max);
		topGroup.addChildReference(reference);
	}

	void createMetadataForOneGroupWithOneLinkAndOtherChildren() {
		addMetadataForOneGroupWithOneLink("test");
		MetadataGroup group = (MetadataGroup) metadataHolder.getMetadataElement("testGroup");

		TextVariable textVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("textVar",
						"textVarNameInData", "textVarTextId", "textVarDefTextId", ".*");
		metadataHolder.addMetadataElement(textVar);

		MetadataChildReference textVarReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(
						"metadataTextVariable", "textVar", 1, 15);
		group.addChildReference(textVarReference);

		MetadataGroup subGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("subGroup",
				"subGroup", "subGroupTextId", "subGroupDefTextId");
		metadataHolder.addMetadataElement(subGroup);
		MetadataChildReference subGroupReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadataGroup",
						"subGroup", 1, 15);
		group.addChildReference(subGroupReference);
	}

	void addMetadataForOneGroupWithOneLinkWithPath() {
		addMetadataForOneGroupWithOneLink("test");

		RecordLink recordLink = (RecordLink) metadataHolder.getMetadataElement("testLink");

		DataGroup linkedPath = new DataGroupOldSpy("linkedPath");
		recordLink.setLinkedPath(linkedPath);
		linkedPath.addChild(new DataAtomicOldSpy("nameInData", "someNameInData"));
	}

	void addMetadataForOneGroupInGroupWithOneLink() {
		addMetadataForOneGroupWithNoLink("top");
		addMetadataForOneGroupWithOneLink("test");
		addChildReferenceParentIdChildRecordTypeIdMinMax("topGroup", "metadataGroup", "testGroup",
				1, 1);
	}

	void addMetadataForOneGroupInGroupInGroupWithOneLink() {
		addMetadataForOneGroupWithNoLink("top");
		addMetadataForOneGroupWithOneLink("test");
		addChildReferenceParentIdChildRecordTypeIdMinMax("topGroup", "metadataGroup", "testGroup",
				1, 1);
		addMetadataForOneGroupWithNoLink("topTop");
		addChildReferenceParentIdChildRecordTypeIdMinMax("topTopGroup", "metadataGroup", "topGroup",
				1, 2);

		CollectionVariable collectionVariable = new CollectionVariable("attribute1", "attribute1",
				"textId", "defTextId", "itemCollectionId");
		metadataHolder.addMetadataElement(collectionVariable);
		collectionVariable.setRefParentId("collectionVariableId");
		collectionVariable.setFinalValue("attrValue");

		MetadataGroup topTopGroup = (MetadataGroup) metadataHolder.getMetadataElement("topGroup");
		topTopGroup.addAttributeReference("attribute1");

	}

	public MetadataHolder getMetadataHolder() {
		return metadataHolder;
	}
}
