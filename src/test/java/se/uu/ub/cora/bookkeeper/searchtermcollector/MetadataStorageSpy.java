package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

import java.util.Collection;

public class MetadataStorageSpy implements MetadataStorage {
    @Override
    public Collection<DataGroup> getMetadataElements() {
        return null;
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
