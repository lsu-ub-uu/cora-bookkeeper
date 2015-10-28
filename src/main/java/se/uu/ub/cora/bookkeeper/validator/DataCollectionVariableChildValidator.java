/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariableChild;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;

public class DataCollectionVariableChildValidator extends DataCollectionVariableValidator {

	public DataCollectionVariableChildValidator(MetadataHolder metadataHolder,
			CollectionVariableChild collectionVariableChild) {
		super(metadataHolder, collectionVariableChild);

	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		DataAtomic data = (DataAtomic) dataElement;
		String finalValue = ((CollectionVariableChild) collectionVariable).getFinalValue();

		if (null != finalValue) {
			// there is a final value, check if it is ok

			if (!finalValue.equals(data.getValue())) {
				validationAnswer.addErrorMessage("Value is not finalValue");
			}
			return validationAnswer;
		}
		return super.validateData(dataElement);
	}
}
