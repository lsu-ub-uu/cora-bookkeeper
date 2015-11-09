package se.uu.ub.cora.bookkeeper.testdata;


import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

public class DataCreator
{
    public static MetadataGroup createMetaDataGroup(String id, MetadataHolder metadataHolder)
    {
        MetadataGroup group = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id + "GroupId",
                id + "GroupNameInData", id +"GroupText", id +"GroupDefText");
        metadataHolder.addMetadataElement(group);
        return group;
    }

    public static void addAttributeReferenceToGroup(MetadataGroup group, String attributeReference){
        group.addAttributeReference(attributeReference);
    }

    public static void addChildReferenceToGroup(MetadataGroup group, MetadataChildReference childReference)
    {
        group.addChildReference(childReference);
    }

    public static void addRecordLinkChildReferenceToGroup(String id, MetadataGroup group, MetadataHolder metadataHolder)
    {
        RecordLink recordLink = RecordLink
                .withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("recordLink" +id,
                        "recordLink" +id, "recordLinkText" +id, "recordLinkDefText" +id,
                        "recordLinkLinkedRecordType");
        metadataHolder.addMetadataElement(recordLink);

        MetadataChildReference linkChild = MetadataChildReference
                .withReferenceIdAndRepeatMinAndRepeatMax("recordLink" +id, 1, 1);

        group.addChildReference(linkChild);
    }

    public static void addUnlimitedTextVarChildReferenceToGroup(String id, MetadataGroup group, MetadataHolder metadataHolder)
    {
        addTextVarChildReferenceToGroup(id, MetadataChildReference.UNLIMITED, group, metadataHolder);

    }

    public static void addOnlyOneTextVarChildReferenceToGroup(String id, MetadataGroup group, MetadataHolder metadataHolder)
    {
        addTextVarChildReferenceToGroup(id, 1, group, metadataHolder);
    }

    public static void addTextVarChildReferenceToGroup(String id, int repeatMax, MetadataGroup group, MetadataHolder metadataHolder)
    {
        TextVariable textVar = TextVariable
                .withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(id + "Id",
                        id + "NameInData", id + "Text", id +"DefText",
                        "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
        metadataHolder.addMetadataElement(textVar);

        MetadataChildReference groupChild = MetadataChildReference
                .withReferenceIdAndRepeatMinAndRepeatMax(id +"Id", 1,
                        repeatMax);
        group.addChildReference(groupChild);
    }

    public static void addDefaultCollectionTwoChoices(String id, MetadataGroup group, MetadataHolder metadataHolder)
    {
        CollectionVariable colVar = new CollectionVariable(id + "CollectionVar",
                id + "CollectionVarNameInData", id + "CollectionVarText", id + "CollectionVarDefText",
                "collectionId");
        metadataHolder.addMetadataElement(colVar);
        group.addAttributeReference(id + "CollectionVar");

        if(metadataHolder.getMetadataElement("choice1Id") == null) {
            CollectionItem choice1 = new CollectionItem("choice1Id", "choice1NameInData",
                    "choice1TextId", "choice1DefTextId");
            metadataHolder.addMetadataElement(choice1);

            CollectionItem choice2 = new CollectionItem("choice2Id", "choice2NameInData",
                    "choice2TextId", "choice2DefTextId");
            metadataHolder.addMetadataElement(choice2);

            ItemCollection collection = new ItemCollection("collectionId", "collectionNameInData",
                    "CollectionTextId", "collectionDefTextId");
            metadataHolder.addMetadataElement(collection);
            collection.addItemReference("choice1Id");
            collection.addItemReference("choice2Id");
        }


    }
}
