/*
 * Copyright 2015, 2019, 2023 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataResourceLink;

class DataResourceLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private MetadataHolder metadataHolder;
	private DataResourceLink dataForResourceLink;

	public DataResourceLinkValidator(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	@Override
	public ValidationAnswer validateData(DataChild dataChild) {
		validationAnswer = new ValidationAnswer();
		dataForResourceLink = (DataResourceLink) dataChild;
		validateNameInData();
		return validationAnswer;
	}

	private void validateNameInData() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataResourceLink must have a nonempty nameInData");
		}
	}

	private boolean nameInDataIsEmpty() {
		return dataForResourceLink.getNameInData().isEmpty();
	}

	MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

}
