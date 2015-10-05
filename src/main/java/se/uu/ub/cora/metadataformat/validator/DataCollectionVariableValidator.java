package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.metadata.CollectionItem;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariable;
import se.uu.ub.cora.metadataformat.metadata.ItemCollection;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;

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

			if (data.getValue().equals(colItem.getNameInData())) {
				valueFoundInCollection = true;
				break;
			}
		}
		if (!valueFoundInCollection) {
			validationAnswer.addErrorMessage("Data value:" + data.getValue()
					+ " NOT found in collection:" + col.getNameInData());
		}
		return validationAnswer;
	}

}
