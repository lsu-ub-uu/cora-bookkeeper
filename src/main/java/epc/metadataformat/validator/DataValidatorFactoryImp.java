package epc.metadataformat.validator;

import epc.metadataformat.metadata.CollectionVariable;
import epc.metadataformat.metadata.CollectionVariableChild;
import epc.metadataformat.metadata.MetadataElement;
import epc.metadataformat.metadata.MetadataGroup;
import epc.metadataformat.metadata.MetadataGroupChild;
import epc.metadataformat.metadata.MetadataHolder;
import epc.metadataformat.metadata.TextVariable;

public class DataValidatorFactoryImp implements DataValidatorFactory {

	private MetadataHolder metadataHolder;

	public DataValidatorFactoryImp(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	@Override
	public DataElementValidator factor(String elementId) {
		MetadataElement metadataElement = metadataHolder.getMetadataElement(elementId);

		if (metadataElement instanceof MetadataGroupChild
				|| metadataElement instanceof MetadataGroup) {
			// same validator for childGroup as group
			return new DataGroupValidator(this, metadataHolder, (MetadataGroup) metadataElement);
		}
		if (metadataElement instanceof TextVariable) {
			return new DataTextVariableValidator((TextVariable) metadataElement);
		}
		if (metadataElement instanceof CollectionVariableChild) {
			return new DataCollectionVariableChildValidator(metadataHolder,
					(CollectionVariableChild) metadataElement);
		}
		if (metadataElement instanceof CollectionVariable) {
			return new DataCollectionVariableValidator(metadataHolder,
					(CollectionVariable) metadataElement);
		}
		throw DataValidationException.withMessage("No validator created for element with id: "
				+ elementId);
	}
}
