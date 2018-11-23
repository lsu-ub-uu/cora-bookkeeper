package se.uu.ub.cora.bookkeeper.metadata;

public class NumberVariable extends MetadataElement {

	private double min;
	private double max;
	private double warningMin;
	private double warningMax;
	private int numOfDecimals;

	private NumberVariable(StandardMetadataParameters standardParams, LimitsContainer limits,
			LimitsContainer warnLimits, int numOfDecimals) {
		super(standardParams.id, standardParams.nameInData, standardParams.textId,
				standardParams.defTextId);
		setNumberSpecificFields(limits, warnLimits, numOfDecimals);
	}

	private void setNumberSpecificFields(LimitsContainer limits, LimitsContainer warnLimits,
			int numOfDecimals) {
		min = limits.min;
		max = limits.max;
		warningMin = warnLimits.min;
		warningMax = warnLimits.max;
		this.numOfDecimals = numOfDecimals;
	}

	public static NumberVariable usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(
			StandardMetadataParameters standardParams, LimitsContainer limits,
			LimitsContainer warnLimits, int numOfDecimals) {
		return new NumberVariable(standardParams, limits, warnLimits, numOfDecimals);
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getWarningMin() {
		return warningMin;
	}

	public double getWarningMax() {
		return warningMax;
	}

	public int getNumOfDecmials() {
		return numOfDecimals;
	}

}
