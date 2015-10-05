package se.uu.ub.cora.metadataformat.validator;

public interface DataValidatorFactory {

	DataElementValidator factor(String elementId);

}
