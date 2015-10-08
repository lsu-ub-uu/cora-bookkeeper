package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.metadata.CollectionVariable;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariableChild;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;
import se.uu.ub.cora.metadataformat.metadata.MetadataElement;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroupChild;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

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
		if (metadataElement instanceof DataToDataLink) {
			return new DataDataToDataLinkValidator();
		}
		throw DataValidationException
				.withMessage("No validator created for element with id: " + elementId);
	}
}
