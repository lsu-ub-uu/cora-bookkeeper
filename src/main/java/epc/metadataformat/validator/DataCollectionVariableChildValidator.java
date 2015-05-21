package epc.metadataformat.validator;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.metadata.CollectionVariableChild;
import epc.metadataformat.metadata.MetadataHolder;

public class DataCollectionVariableChildValidator extends DataCollectionVariableValidator {

	public DataCollectionVariableChildValidator(MetadataHolder metadataHolder,
			CollectionVariableChild collectionVariableChild) {
		super(metadataHolder, collectionVariableChild);

	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		DataAtomic data = (DataAtomic) dataElement;
		String finalValue = ((CollectionVariableChild) collectionVariable).getFinalValue();

		if (null != finalValue) {
			// there is a final value, check if it is ok

			if (!finalValue.equals(data.getValue())) {
				validationAnswer.addErrorMessage("Value is not finalValue");
			}
			return validationAnswer;
		}
		return super.validateData(dataElement);
	}
}
