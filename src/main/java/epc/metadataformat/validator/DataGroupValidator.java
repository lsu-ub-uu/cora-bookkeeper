package epc.metadataformat.validator;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariable;
import epc.metadataformat.metadata.MetadataChildReference;
import epc.metadataformat.metadata.MetadataElement;
import epc.metadataformat.metadata.MetadataGroup;
import epc.metadataformat.metadata.MetadataHolder;

class DataGroupValidator implements DataElementValidator {

	private DataValidatorFactoryImp dataValidatorFactoryImp;
	private final MetadataGroup metadataGroup;
	private MetadataHolder metadataHolder;
	private DataGroup dataGroup;
	private ValidationAnswer validationAnswer;

	public DataGroupValidator(DataValidatorFactoryImp dataValidatorFactoryImp,
			MetadataHolder metadataHolder, MetadataGroup metadataGroup) {
		this.dataValidatorFactoryImp = dataValidatorFactoryImp;
		this.metadataHolder = metadataHolder;
		this.metadataGroup = metadataGroup;
	}

	/**
	 * validateData validates that the entered dataGroup is correct according to this validators
	 * metadataGroup
	 * 
	 * @param dataGroup
	 *            A DataGroup to validate
	 * @return A ValidationAnswer with information if the dataGroup has valid data and if not a list
	 *         of errors
	 */
	@Override
	public ValidationAnswer validateData(DataElement dataGroup) {
		this.dataGroup = (DataGroup) dataGroup;
		validationAnswer = new ValidationAnswer();
		validateNameInData();
		validateAttributes();
		validateChildren();
		return validationAnswer;
	}

	private void validateNameInData() {
		String metadataNameInData = metadataGroup.getNameInData();
		String dataNameInData = dataGroup.getNameInData();
		if (!metadataNameInData.equals(dataNameInData)) {
			validationAnswer.addErrorMessage("DataGroup should have name(nameInData): "
					+ metadataGroup.getNameInData() + " it does not.");
		}
	}

	private void validateAttributes() {
		validateDataContainsAllRequiredAttributesWithCorrectValues();
		validateDataContainsNoUnspecifiedAttributes();
	}

	private void validateDataContainsAllRequiredAttributesWithCorrectValues() {
		Collection<String> mdAttributeReferences = metadataGroup.getAttributeReferences();
		for (String mdAttributeReference : mdAttributeReferences) {
			validateDataContainsAttributeReferenceWithCorrectData(mdAttributeReference);
		}
	}

	private void validateDataContainsAttributeReferenceWithCorrectData(String mdAttributeReference) {
		String nameInData = getNameInDataForAttributeReference(mdAttributeReference);

		Map<String, String> dataAttributes = dataGroup.getAttributes();
		boolean dataAttributesContainsValueForAttribute = dataAttributes.containsKey(nameInData);
		if (dataAttributesContainsValueForAttribute) {
			DataAtomic dataElement = createDataAtomicFromAttribute(mdAttributeReference,
					dataAttributes);
			validateAttribute(mdAttributeReference, dataElement);
		} else {
			validationAnswer.addErrorMessage("Attribute with nameInData: " + nameInData
					+ " does not exist in data.");
		}
	}

	private DataAtomic createDataAtomicFromAttribute(String mdAttributeReference,
			Map<String, String> dataAttributes) {
		String nameInData = getNameInDataForAttributeReference(mdAttributeReference);
		String value = dataAttributes.get(nameInData);
		return DataAtomic.withNameInDataAndValue(nameInData, value);
	}

	private void validateAttribute(String mdAttributeReference, DataAtomic dataElement) {
		DataElementValidator attributeValidator = dataValidatorFactoryImp
				.factor(mdAttributeReference);
		ValidationAnswer aValidationAnswer = attributeValidator.validateData(dataElement);
		addMessagesFromAnswerToTotalValidationAnswer(aValidationAnswer);
	}

	private void addMessagesFromAnswerToTotalValidationAnswer(ValidationAnswer aValidationAnswer) {
		validationAnswer.addErrorMessages(aValidationAnswer.getErrorMessages());
	}

	private String getNameInDataForAttributeReference(String mdAttributeReference) {
		CollectionVariable mdAttribute = (CollectionVariable) metadataHolder
				.getMetadataElement(mdAttributeReference);
		return mdAttribute.getNameInData();
	}

	private void validateDataContainsNoUnspecifiedAttributes() {
		Map<String, String> dAttributes = dataGroup.getAttributes();
		for (Entry<String, String> attribute : dAttributes.entrySet()) {
			String nameInDataFromDataAttribute = attribute.getKey();
			validateNameInDataFromDataAttributeIsSpecifiedInMetadata(nameInDataFromDataAttribute);
		}
	}

	private void validateNameInDataFromDataAttributeIsSpecifiedInMetadata(String dataNameInData) {
		if (!isNameInDataFromDataSpecifiedInMetadata(dataNameInData)) {
			validationAnswer.addErrorMessage("Data attribute with id: " + dataNameInData
					+ " does not exist in metadata.");
		}
	}

	private boolean isNameInDataFromDataSpecifiedInMetadata(String dataNameInData) {
		Collection<String> mdAttributeReferences = metadataGroup.getAttributeReferences();
		for (String mdAttributeReference : mdAttributeReferences) {
			String metadataNameInData = getNameInDataForAttributeReference(mdAttributeReference);
			if (dataNameInData.equals(metadataNameInData)) {
				return true;
			}
		}
		return false;
	}

	private void validateChildren() {
		validateDataContainsAllRequiredChildrenWithCorrectValues();
		validateDataContainsNoUnspecifiedChildren();
	}

	private void validateDataContainsAllRequiredChildrenWithCorrectValues() {
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference childReference : childReferences) {
			validateDataContainsRequiredChildReferenceWithCorrectValue(childReference);
		}
	}

	private void validateDataContainsRequiredChildReferenceWithCorrectValue(
			MetadataChildReference childReference) {
		String referenceId = childReference.getReferenceId();
		int childrenFound = validateAndCountChildrenWithReferenceId(referenceId);
		validateRepeatMinAndMax(childReference, childrenFound);
	}

	private void validateRepeatMinAndMax(MetadataChildReference childReference, int childrenFound) {
		String referenceId = childReference.getReferenceId();
		if (childrenFound < childReference.getRepeatMin()) {
			validationAnswer.addErrorMessage("Did not find enough data children with referenceId: "
					+ referenceId + ".");
		}
		if (childrenFound > childReference.getRepeatMax()) {
			validationAnswer.addErrorMessage("Found too many data children with referenceId: "
					+ referenceId + ".");
		}
	}

	private int validateAndCountChildrenWithReferenceId(String referenceId) {
		DataElementValidator childValidator = dataValidatorFactoryImp.factor(referenceId);
		int childrenFound = 0;
		for (DataElement childData : dataGroup.getChildren()) {
			if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
				childrenFound++;
				validateChildElementData(childValidator, childData);
			}
		}
		return childrenFound;
	}

	private boolean isChildDataSpecifiedByChildReferenceId(DataElement childData, String referenceId) {
		MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
		if (childElement.getNameInData().equals(childData.getNameInData())) {
			return true;
		}
		return false;
	}

	private void validateChildElementData(DataElementValidator childValidator, DataElement childData) {
		ValidationAnswer va = childValidator.validateData(childData);
		addMessagesFromAnswerToTotalValidationAnswer(va);
	}

	private void validateDataContainsNoUnspecifiedChildren() {
		for (DataElement childData : dataGroup.getChildren()) {
			if (!isChildDataSpecifiedInMetadataGroup(childData)) {
				validationAnswer.addErrorMessage("Metadata for child with " + "nameInData: "
						+ childData.getNameInData() + " can not be found in metadata.");
			}
		}
	}

	private boolean isChildDataSpecifiedInMetadataGroup(DataElement childData) {
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference childReference : childReferences) {
			if (isChildDataSpecifiedByChildReferenceId(childData, childReference.getReferenceId())) {
				return true;
			}
		}
		return false;
	}
}
