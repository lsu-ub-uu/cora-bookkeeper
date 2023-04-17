package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.bookkeeper.metadata.LimitsContainer;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.bookkeeper.metadata.StandardMetadataParameters;

public class NumberVariableSpy extends NumberVariable {
	private static StandardMetadataParameters sp = StandardMetadataParameters
			.usingIdNameInDataAndTextContainer(null, null, null);
	private static LimitsContainer limit = LimitsContainer.usingMinAndMax(0, 0);

	public NumberVariableSpy() {
		super(sp, limit, limit, 0);
	}
}
