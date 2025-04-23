/*
 * Copyright 2015, 2017, 2019, 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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

package se.uu.ub.cora.bookkeeper.decorator;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.text.TextHolder;
import se.uu.ub.cora.data.DataGroup;

class DataChildDecoratorFactoryImp implements DataChildDecoratorFactory {

	private Map<String, DataGroup> recordTypeHolder;
	private MetadataHolder metadataHolder;
	private TextHolder textHolder;

	public DataChildDecoratorFactoryImp(Map<String, DataGroup> recordTypeHolder,
			MetadataHolder metadataHolder, TextHolder textHolder) {
		this.recordTypeHolder = recordTypeHolder;
		this.metadataHolder = metadataHolder;
		this.textHolder = textHolder;
	}

	@Override
	public DataChildDecorator factor(String elementId) {
		MetadataElement metadataElement = metadataHolder.getMetadataElement(elementId);

		// if (metadataElement instanceof TextVariable) {
		return new DataGenericDecorator(metadataElement, textHolder);
		// }

		// if (metadataElement instanceof MetadataGroup metadataElementGroup) {
		// return new DataGroupValidator(this, metadataHolder, metadataElementGroup);
		// }
		// if (metadataElement instanceof TextVariable metadataElementTextVariable) {
		// return new DataTextVariableValidator(metadataElementTextVariable);
		// }
		// if (metadataElement instanceof NumberVariable metadataElementNumberVariable) {
		// return new DataNumberVariableValidator(metadataElementNumberVariable);
		// }
		// if (metadataElement instanceof CollectionVariable metadataElementCollectionVariable) {
		// return new DataCollectionVariableValidator(metadataHolder,
		// metadataElementCollectionVariable);
		// }
		// if (metadataElement instanceof RecordLink metadataElementRecordLink) {
		// return new DataRecordLinkValidator(recordTypeHolder, metadataHolder,
		// metadataElementRecordLink);
		// }
		// if (metadataElement instanceof ResourceLink) {
		// return new DataResourceLinkValidator(metadataHolder);
		// }
		// throw DataValidationException
		// .withMessage("No validator created for element with id: " + elementId);
	}

	MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

	Map<String, DataGroup> onlyForTestGetRecordTypeHolder() {
		return recordTypeHolder;
	}
}
