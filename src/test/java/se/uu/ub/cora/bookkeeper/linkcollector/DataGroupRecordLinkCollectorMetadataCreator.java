package se.uu.ub.cora.bookkeeper.linkcollector;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.*;

public class DataGroupRecordLinkCollectorMetadataCreator {
    private MetadataHolder metadataHolder = new MetadataHolder();


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

        addChildReferenceParentIdChildIdMinMax(id + "Group", id + "Link", 1, 15);
    }

    void addChildReferenceParentIdChildIdMinMax(String from, String to, int min, int max) {
        MetadataGroup topGroup = (MetadataGroup) metadataHolder.getMetadataElement(from);

        MetadataChildReference reference = MetadataChildReference
                .withReferenceIdAndRepeatMinAndRepeatMax(to, min, max);
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
                .withReferenceIdAndRepeatMinAndRepeatMax("textVar", 1, 15);
        group.addChildReference(textVarReference);

        MetadataGroup subGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("subGroup",
                "subGroup", "subGroupTextId", "subGroupDefTextId");
        metadataHolder.addMetadataElement(subGroup);
        MetadataChildReference subGroupReference = MetadataChildReference
                .withReferenceIdAndRepeatMinAndRepeatMax("subGroup", 1, 15);
        group.addChildReference(subGroupReference);
    }

    void addMetadataForOneGroupWithOneLinkWithPath() {
        addMetadataForOneGroupWithOneLink("test");

        RecordLink recordLink = (RecordLink) metadataHolder.getMetadataElement("testLink");

        DataGroup linkedPath = DataGroup.withNameInData("linkedPath");
        recordLink.setLinkedPath(linkedPath);
        linkedPath.addChild(DataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
    }

    void addMetadataForOneGroupInGroupWithOneLink() {
        addMetadataForOneGroupWithNoLink("top");
        addMetadataForOneGroupWithOneLink("test");
        addChildReferenceParentIdChildIdMinMax("topGroup", "testGroup", 1, 1);
    }

    void addMetadataForOneGroupInGroupInGroupWithOneLink() {
        addMetadataForOneGroupWithNoLink("top");
        addMetadataForOneGroupWithOneLink("test");
        addChildReferenceParentIdChildIdMinMax("topGroup", "testGroup", 1, 1);
        addMetadataForOneGroupWithNoLink("topTop");
        addChildReferenceParentIdChildIdMinMax("topTopGroup", "topGroup", 1, 2);

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
