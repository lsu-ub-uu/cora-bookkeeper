package epc.metadataformat.validator;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.metadata.CollectionItem;
import epc.metadataformat.metadata.CollectionVariable;
import epc.metadataformat.metadata.ItemCollection;
import epc.metadataformat.metadata.MetadataHolder;

public class DataCollectionVariableValidator implements DataElementValidator {

	protected MetadataHolder metadataHolder;
	protected CollectionVariable collectionVariable;

	public DataCollectionVariableValidator(MetadataHolder metadataHolder,
			CollectionVariable collectionVariable) {
		this.metadataHolder = metadataHolder;
		this.collectionVariable = collectionVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		DataAtomic data = (DataAtomic) dataElement;
		ValidationAnswer validationAnswer = new ValidationAnswer();
		ItemCollection col = (ItemCollection) metadataHolder.getMetadataElement(collectionVariable
				.getRefCollectionId());

		boolean valueFoundInCollection = false;
		for (String ref : col.getCollectionItemReferences()) {
			CollectionItem colItem = (CollectionItem) metadataHolder.getMetadataElement(ref);

			if (data.getValue().equals(colItem.getDataId())) {
				valueFoundInCollection = true;
				break;
			}
		}
		if (!valueFoundInCollection) {
			validationAnswer.addErrorMessage("Data value:" + data.getValue()
					+ " NOT found in collection:" + col.getDataId());
		}
		return validationAnswer;
	}

}
