package epc.metadataformat.validator;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.metadata.TextVariable;

public class DataTextVariableValidator implements DataElementValidator {

	private TextVariable textVariable;

	public DataTextVariableValidator(TextVariable textVariable) {
		this.textVariable = textVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		DataAtomic dataAtomic = (DataAtomic) dataElement;
		ValidationAnswer validationAnswer = new ValidationAnswer();
		if (!dataIsValidAccordingToRegEx(dataAtomic)) {
			validationAnswer.addErrorMessage("TextVariable with nameInData:" + dataAtomic.getNameInData()
					+ " is NOT valid, regular expression(" + textVariable.getRegularExpression()
					+ ") does not match:" + dataAtomic.getValue());
		}
		return validationAnswer;
	}

	private boolean dataIsValidAccordingToRegEx(DataAtomic dataElement) {
		return dataElement.getValue().matches(textVariable.getRegularExpression());
	}
}
