package se.uu.ub.cora.bookkeeper.metadata;

/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
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
