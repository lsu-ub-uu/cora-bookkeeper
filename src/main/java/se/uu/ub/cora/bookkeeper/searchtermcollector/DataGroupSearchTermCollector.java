package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public interface DataGroupSearchTermCollector {
    DataGroup collectSearchTerms(String metadataGroupId, DataGroup dataGroup);
}
