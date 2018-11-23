package se.uu.ub.cora.bookkeeper.metadata;

public class LimitsContainer {

	public double min;
	public double max;

	public LimitsContainer(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public static LimitsContainer usingMinAndMax(double min, double max) {
		return new LimitsContainer(min, max);
	}

}
