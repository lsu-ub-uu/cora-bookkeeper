package epc.metadataformat.metadata;

public enum MetadataTypes {
	GROUP("metadataGroup"), 
	TEXTVARIABLE("metadataTextVariable"), 
	COLLECTIONVARIABLE("metadataCollectionVariable"), 
	ITEMCOLLECTION("metadataItemCollection"), 
	COLLECTIONITEM("metadataCollectionItem");
	
	private MetadataTypes(String type) {
		this.type = type;
	}

	public final String type;
}
