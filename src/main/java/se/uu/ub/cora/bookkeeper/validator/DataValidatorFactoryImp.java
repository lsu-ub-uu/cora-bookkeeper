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

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.ResourceLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

public class DataValidatorFactoryImp implements DataValidatorFactory {

	private MetadataHolder metadataHolder;

	public DataValidatorFactoryImp(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	@Override
	public DataElementValidator factor(String elementId) {
		MetadataElement metadataElement = metadataHolder.getMetadataElement(elementId);

		if (metadataElement instanceof MetadataGroup) {
			return new DataGroupValidator(this, metadataHolder, (MetadataGroup) metadataElement);
		}
		if (metadataElement instanceof TextVariable) {
			return new DataTextVariableValidator((TextVariable) metadataElement);
		}
		if (metadataElement instanceof CollectionVariable) {
			return new DataCollectionVariableValidator(metadataHolder,
					(CollectionVariable) metadataElement);
		}
		if (metadataElement instanceof RecordLink) {
			return new DataRecordLinkValidator(metadataHolder, (RecordLink) metadataElement);
		}
		if (metadataElement instanceof ResourceLink) {
			return new DataResourceLinkValidator(metadataHolder);
		}
		throw DataValidationException
				.withMessage("No validator created for element with id: " + elementId);
	}
}
