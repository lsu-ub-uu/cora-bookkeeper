package se.uu.ub.cora.bookkeeper.metadata;

import java.util.List;

public interface MetadataElement {

	String getId();

	String getNameInData();

	String getTextId();

	String getDefTextId();

	List<String> getAttributeReferences();

}