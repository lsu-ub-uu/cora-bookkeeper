package epc.metadataformat.validator;

public interface DataValidatorFactory {

	DataElementValidator factor(String elementId);

}
