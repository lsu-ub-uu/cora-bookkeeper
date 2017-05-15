package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class TestDataCreator {
    public static DataGroup createMetadataGroupWithIdAndNameInDataAndTypeAndDataDivider(String id,
                                                                                        String nameInData, String type, String dataDividerId) {
        DataGroup metadataGroup = DataGroup.withNameInData(nameInData);
        DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
        recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
        createAndAddType(type, recordInfo);

        recordInfo.addChild(createDataDivider(dataDividerId));
        metadataGroup.addChild(recordInfo);
        return metadataGroup;
    }

    private static void createAndAddType(String type, DataGroup recordInfo) {
        DataGroup typeGroup = DataGroup.withNameInData("type");
        typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
        typeGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", type));
        recordInfo.addChild(typeGroup);
    }


    private static DataGroup createDataDivider(String dataDividerId) {
        DataGroup dataDivider = DataGroup.withNameInData("dataDivider");
        dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
        dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", dataDividerId));
        return dataDivider;
    }

//    public static DataGroup createRecordTypeWithMetadataId(String recordId, String metadataId) {
//        DataGroup collectionItem = createMetadataGroupWithIdAndNameInDataAndTypeAndDataDivider(
//                recordId, "recordType", "recordType", "cora");
//        DataGroup metadataIdGroup = DataGroup.withNameInData("metadataId");
//        metadataIdGroup
//                .addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
//        metadataIdGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", metadataId));
//        collectionItem.addChild(metadataIdGroup);
//        return collectionItem;
//    }
}
