package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.data.Data;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetadataStorageSpy implements MetadataStorage {
    private List<DataGroup> dataGroups;

    @Override
    public Collection<DataGroup> getMetadataElements() {
        dataGroups = new ArrayList<>();

        DataGroup book = createBookDataGroup();
        dataGroups.add(book);

        DataGroup searchTerm = createSearchTerm();
        dataGroups.add(searchTerm);

        DataGroup searchTitleTextVar = createTitleTextVar();
        dataGroups.add(searchTitleTextVar);
        return dataGroups;
    }


    private DataGroup createSearchTerm() {
        DataGroup searchTerm = TestDataCreator.createMetadataGroupWithIdAndNameInDataAndTypeAndDataDivider
                ("titleSearchTerm", "searchTerm", "searchTerm", "testSystem");
        searchTerm.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
        DataGroup searchFieldRef = DataGroup.withNameInData("searchFieldRef");
        searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
        searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));
        searchTerm.addChild(searchFieldRef);
        return searchTerm;
    }

    private DataGroup createBookDataGroup() {
        DataGroup book = TestDataCreator.createMetadataGroupWithIdAndNameInDataAndTypeAndDataDivider
                ("bookGroup", "metadata", "metadataGroup", "testSystem");
        book.addAttributeByIdWithValue("type", "group");
        book.addChild(DataAtomic.withNameInDataAndValue("nameInData", "book"));
        DataGroup childReferences = createChildReferencesForBook();
        book.addChild(childReferences);
        return book;
    }

    private DataGroup createChildReferencesForBook() {
        DataGroup childReferences = DataGroup.withNameInData("childReferences");
        DataGroup childReference = DataGroup.withNameInData("childReference");
        childReference.setRepeatId("0");
        childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "1"));
        childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "1"));
        DataGroup ref = DataGroup.withNameInData("ref");
        ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
        ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "bookTitleTextVar"));
        childReference.addChild(ref);

        DataGroup childRefSearchTerm = DataGroup.withNameInData("childRefSearchTerm");
        childRefSearchTerm.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "searchTerm"));
        childRefSearchTerm.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "titleSearchTerm"));
        childRefSearchTerm.setRepeatId("1");
        childReference.addChild(childRefSearchTerm);
        childReferences.addChild(childReference);
        return childReferences;
    }

    private DataGroup createTitleTextVar() {
        DataGroup searchTitleTextVar = TestDataCreator.createMetadataGroupWithIdAndNameInDataAndTypeAndDataDivider
                ("searchTitleTextVar", "metadata", "metadataTextVariable", "testSystem");
        searchTitleTextVar.addChild(DataAtomic.withNameInDataAndValue("nameInData", "searchTitle"));
        searchTitleTextVar.addChild(DataAtomic.withNameInDataAndValue("regEx", "(^[0-9A-ZÅÄÖ a-zåäö:-_]{2,100}$)"));
        return searchTitleTextVar;
    }

    @Override
    public Collection<DataGroup> getPresentationElements() {
        return null;
    }

    @Override
    public Collection<DataGroup> getTexts() {
        return null;
    }

    @Override
    public Collection<DataGroup> getRecordTypes() {
        return null;
    }


}
