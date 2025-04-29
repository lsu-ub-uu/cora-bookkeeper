/*
 * Copyright 2025 Uppsala University Library
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

import java.util.Collection;
import java.util.Iterator;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataFactory;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

class DataGroupDecorator implements DataChildDecorator {

	private DataChildDecoratorFactory dataDecoratorFactory;
	private final MetadataGroup metadataGroup;
	private MetadataHolder metadataHolder;
	protected ValidationAnswer validationAnswer;
	private MetadataMatchDataFactory metadataMatchFactory;

	DataGroupDecorator(DataChildDecoratorFactory dataDecoratorFactory,
			MetadataMatchDataFactory metadataMatchFactory, MetadataGroup metadataGroup) {
		this.dataDecoratorFactory = dataDecoratorFactory;
		this.metadataMatchFactory = metadataMatchFactory;
		this.metadataGroup = metadataGroup;
	}

	@Override
	public void decorateData(DataChild dataGroup) {
		metadataHolder = MetadataHolderProvider.getHolder();
		decorateAllChildren((DataGroup) dataGroup);
	}

	private void decorateAllChildren(DataGroup dataGroup) {
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (DataChild childData : dataGroup.getChildren()) {
			decorateChild(childData, childReferences);
		}
	}

	private void decorateChild(DataChild childData,
			Collection<MetadataChildReference> childReferences) {
		boolean matched = false;
		var iterator = childReferences.iterator();
		while (iterateChildReferencesUntilChildIsMatched(matched, iterator)) {
			matched = decorateChildIfMatchingMetadata(childData, iterator.next());
		}
	}

	private boolean iterateChildReferencesUntilChildIsMatched(boolean found,
			Iterator<MetadataChildReference> iterator) {
		return !found && iterator.hasNext();
	}

	private boolean decorateChildIfMatchingMetadata(DataChild childData,
			MetadataChildReference childReference) {
		String metadataId = childReference.getLinkedRecordId();
		if (isChildDataSpecifiedByChildReferenceId(childData, metadataId)) {
			DataChildDecorator decorator = dataDecoratorFactory.factor(metadataId);
			decorator.decorateData(childData);
			return true;
		}
		return false;
	}

	private boolean isChildDataSpecifiedByChildReferenceId(DataChild childData,
			String referenceId) {
		MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
		MetadataMatchData metadataMatchData = metadataMatchFactory.factor();
		return metadataMatchData.metadataSpecifiesData(childElement, childData).dataIsValid();
	}

	DataChildDecoratorFactory onlyForTestGetDataElementValidatorFactory() {
		return dataDecoratorFactory;
	}

	MetadataMatchDataFactory onlyForTestGetMetadataMatchFactory() {
		return metadataMatchFactory;
	}

	MetadataElement onlyForTestGetMetadataElement() {
		return metadataGroup;
	}

}
