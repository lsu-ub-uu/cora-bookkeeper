/*
 * Copyright 2026 Uppsala University Library
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

import java.util.HashSet;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataProvider;

public class DataFilterCreatorImp implements DataFilterCreator {

	public static DataFilterCreatorImp usingMetadataHolder(MetadataHolder metadataHolder) {
		return new DataFilterCreatorImp(metadataHolder);
	}

	private MetadataHolder metadataHolder;

	private DataFilterCreatorImp(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;

	}

	@Override
	public DataChildFilter createDataChildFilterFromMetadata(MetadataElement metadataElement) {
		DataChildFilter filter = DataProvider
				.createDataChildFilterUsingChildNameInData(metadataElement.getNameInData());
		addAllAttributesToFilter(metadataElement, filter);
		return filter;
	}

	private void addAllAttributesToFilter(MetadataElement metadataElement, DataChildFilter filter) {
		for (String attributeRef : metadataElement.getAttributeReferences()) {
			addAttributeToFilter(filter, attributeRef);
		}
	}

	private void addAttributeToFilter(DataChildFilter filter, String attributeRef) {
		CollectionVariable attributeElement = (CollectionVariable) metadataHolder
				.getMetadataElement(attributeRef);
		if (attributeHasFinalValue(attributeElement)) {
			addAttributeWithFinalValueToFilter(filter, attributeElement);
		} else {
			addAttributeWithItemsToFilter(filter, attributeElement);
		}
	}

	private boolean attributeHasFinalValue(CollectionVariable attributeElement) {
		return null != attributeElement.getFinalValue();
	}

	private void addAttributeWithFinalValueToFilter(DataChildFilter filter,
			CollectionVariable attributeElement) {
		filter.addAttributeUsingNameInDataAndPossibleValues(attributeElement.getNameInData(),
				Set.of(attributeElement.getFinalValue()));
	}

	private void addAttributeWithItemsToFilter(DataChildFilter filter,
			CollectionVariable attributeElement) {
		Set<String> possibleValues = getAllAttibuteValues(attributeElement);
		filter.addAttributeUsingNameInDataAndPossibleValues(attributeElement.getNameInData(),
				possibleValues);
	}

	private Set<String> getAllAttibuteValues(CollectionVariable attributeElement) {
		ItemCollection itemCollection = (ItemCollection) metadataHolder
				.getMetadataElement(attributeElement.getRefCollectionId());
		return getAllItemsFromCollection(itemCollection);
	}

	private Set<String> getAllItemsFromCollection(ItemCollection itemCollection) {
		Set<String> possibleValues = new HashSet<>();
		for (String collectionItemRef : itemCollection.getCollectionItemReferences()) {
			CollectionItem collectionItem = (CollectionItem) metadataHolder
					.getMetadataElement(collectionItemRef);
			possibleValues.add(collectionItem.getNameInData());
		}
		return Set.copyOf(possibleValues);
	}

	public Object onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

}
