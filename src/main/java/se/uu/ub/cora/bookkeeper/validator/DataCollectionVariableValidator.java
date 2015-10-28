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
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;

public class DataCollectionVariableValidator implements DataElementValidator {

	protected MetadataHolder metadataHolder;
	protected CollectionVariable collectionVariable;

	public DataCollectionVariableValidator(MetadataHolder metadataHolder,
			CollectionVariable collectionVariable) {
		this.metadataHolder = metadataHolder;
		this.collectionVariable = collectionVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		DataAtomic data = (DataAtomic) dataElement;
		ValidationAnswer validationAnswer = new ValidationAnswer();
		ItemCollection col = (ItemCollection) metadataHolder.getMetadataElement(collectionVariable
				.getRefCollectionId());

		boolean valueFoundInCollection = false;
		for (String ref : col.getCollectionItemReferences()) {
			CollectionItem colItem = (CollectionItem) metadataHolder.getMetadataElement(ref);

			if (data.getValue().equals(colItem.getNameInData())) {
				valueFoundInCollection = true;
				break;
			}
		}
		if (!valueFoundInCollection) {
			validationAnswer.addErrorMessage("Data value:" + data.getValue()
					+ " NOT found in collection:" + col.getNameInData());
		}
		return validationAnswer;
	}

}
