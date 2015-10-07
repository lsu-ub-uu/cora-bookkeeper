package se.uu.ub.cora.metadataformat.metadata;

public final class DataToDataLink extends MetadataElement
{
	private String targetRecordType;

	public static DataToDataLink withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType(
			String id, String nameInData, String textId, String defTextId,
			String targetRecordType)
	{
		return new DataToDataLink(id, nameInData, textId, defTextId, targetRecordType);
	}

	private DataToDataLink(String id, String nameInData, String textId, String defTextId,
						   String targetRecordType)
	{
		super(id, nameInData, textId, defTextId);
		this.targetRecordType = targetRecordType;
	}

	public String getTargetRecordType()
	{
		return targetRecordType;
	}

}
