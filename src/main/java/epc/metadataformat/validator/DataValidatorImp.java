package epc.metadataformat.validator;

import java.util.Collection;

import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataHolder;
import epc.metadataformat.metadata.converter.DataGroupToMetadataConverter;
import epc.metadataformat.metadata.converter.DataGroupToMetadataConverterFactory;
import epc.metadataformat.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import epc.metadataformat.storage.MetadataStorage;

/**
 * ValidateData is a class to validate if a set of data is valid according to its metadataFormat
 * 
 * @author olov
 * 
 */
public class DataValidatorImp implements DataValidator {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;
	private String metadataId;
	private DataElement dataElement;

	public DataValidatorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public ValidationAnswer validateData(String metadataId, DataElement dataElement) {
		this.metadataId = metadataId;
		this.dataElement = dataElement;
		try {
			return tryToValidateData();
		} catch (Exception exception) {
			ValidationAnswer validationAnswer = new ValidationAnswer();
			validationAnswer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage(
					"DataElementValidator not created for the requested metadataId: " + metadataId
							+ " with error:", exception);
			return validationAnswer;
		}
	}

	private ValidationAnswer tryToValidateData() {
		getMetadataFromStorage();
		return validateDataUsingDataValidator();
	}

	private void getMetadataFromStorage() {
		metadataHolder = new MetadataHolder();
		Collection<DataGroup> metadataElementDataGroups = metadataStorage.getMetadataElements();
		convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(metadataElementDataGroups);
	}

	private void convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(
			Collection<DataGroup> metadataElements) {
		for (DataGroup metadataElement : metadataElements) {
			convertDataGroupToMetadataElementAndAddItToMetadataHolder(metadataElement);
		}
	}

	private void convertDataGroupToMetadataElementAndAddItToMetadataHolder(DataGroup metadataElement) {
		DataGroupToMetadataConverterFactory factory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(metadataElement);
		DataGroupToMetadataConverter converter = factory.factor();
		metadataHolder.addMetadataElement(converter.toMetadata());
	}

	private ValidationAnswer validateDataUsingDataValidator() {
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator elementValidator = dataValidatorFactory.factor(metadataId);
		return elementValidator.validateData(dataElement);
	}
}
