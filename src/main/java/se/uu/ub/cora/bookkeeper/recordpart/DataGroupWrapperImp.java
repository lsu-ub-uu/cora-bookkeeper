/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperImp implements DataGroup, DataGroupWrapper {
	Map<String, List<List<DataAttribute>>> removedElements = new HashMap<>();
	DataGroup dataGroup;

	public DataGroupWrapperImp(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		ensurePlaceForNameInDataExistsInMap(childNameInData);
		removedElements.get(childNameInData).add(Arrays.asList(childAttributes));
		return dataGroup.removeAllChildrenWithNameInDataAndAttributes(childNameInData,
				childAttributes);
	}

	private void ensurePlaceForNameInDataExistsInMap(String childNameInData) {
		if (!removedElements.containsKey(childNameInData)) {
			removedElements.put(childNameInData, new ArrayList<>());
		}
	}

	@Override
	public void setRepeatId(String repeatId) {
		dataGroup.setRepeatId(repeatId);
	}

	@Override
	public String getRepeatId() {
		return dataGroup.getRepeatId();
	}

	@Override
	public String getNameInData() {
		return dataGroup.getNameInData();
	}

	@Override
	public boolean hasChildren() {
		return dataGroup.hasChildren();
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		return dataGroup.containsChildWithNameInData(nameInData);
	}

	@Override
	public void addChild(DataElement dataElement) {
		dataGroup.addChild(dataElement);
	}

	@Override
	public void addChildren(Collection<DataElement> dataElements) {
		dataGroup.addChildren(dataElements);
	}

	@Override
	public List<DataElement> getChildren() {
		return dataGroup.getChildren();
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInData(String nameInData) {
		return dataGroup.getAllChildrenWithNameInData(nameInData);
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		return dataGroup.getAllChildrenWithNameInDataAndAttributes(nameInData, childAttributes);
	}

	@Override
	public DataElement getFirstChildWithNameInData(String nameInData) {
		return dataGroup.getFirstChildWithNameInData(nameInData);
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		return dataGroup.getFirstAtomicValueWithNameInData(nameInData);
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return dataGroup.getAllDataAtomicsWithNameInData(childNameInData);
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		return dataGroup.getFirstGroupWithNameInData(childNameInData);
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		return dataGroup.getAllGroupsWithNameInData(nameInData);
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return dataGroup.getAllGroupsWithNameInDataAndAttributes(childNameInData, childAttributes);
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		ensurePlaceForNameInDataExistsInMap(childNameInData);
		removedElements.get(childNameInData).add(Collections.emptyList());
		return dataGroup.removeFirstChildWithNameInData(childNameInData);
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		ensurePlaceForNameInDataExistsInMap(childNameInData);
		removedElements.get(childNameInData).add(Collections.emptyList());
		return dataGroup.removeAllChildrenWithNameInData(childNameInData);
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		return dataGroup.getFirstDataAtomicWithNameInData(childNameInData);
	}

	DataGroup getDataGroup() {
		return dataGroup;
	}

	@Override
	public boolean hasRemovedBeenCalled(DataElement child) {
		if (removeHasNotBeenCalledForNameInData(child)) {
			return false;
		}
		return removeHasBeenCalledForChildsAttributes(child);
	}

	private boolean removeHasNotBeenCalledForNameInData(DataElement child) {
		return !removedElements.containsKey(child.getNameInData());
	}

	private boolean removeHasBeenCalledForChildsAttributes(DataElement child) {
		String nameInData = child.getNameInData();

		List<List<DataAttribute>> allRemovedAttributes = removedElements.get(nameInData);
		Collection<DataAttribute> childAttributes = child.getAttributes();
		return attributeCollectionContainsSameElementsAsAtLeastOneInListOfLists(childAttributes,
				allRemovedAttributes);
	}

	private boolean attributeCollectionContainsSameElementsAsAtLeastOneInListOfLists(
			Collection<DataAttribute> childAttributes,
			List<List<DataAttribute>> allRemovedAttributes) {
		for (List<DataAttribute> removedAttributes : allRemovedAttributes) {
			if (attributeCollectionsContainsSameElements(childAttributes, removedAttributes)) {
				return true;
			}
		}
		return false;
	}

	private boolean attributeCollectionsContainsSameElements(Collection<DataAttribute> attributes1,
			Collection<DataAttribute> attributes2) {
		if (collectionsAreDifferentSizes(attributes1, attributes2)) {
			return false;
		}
		return allAttributesInFirstCollectionExistsInSecond(attributes1, attributes2);

	}

	private boolean collectionsAreDifferentSizes(Collection<DataAttribute> firstCol,
			Collection<DataAttribute> secondCol) {
		return firstCol.size() != secondCol.size();
	}

	private boolean allAttributesInFirstCollectionExistsInSecond(Collection<DataAttribute> firstCol,
			Collection<DataAttribute> secondCol) {
		for (DataAttribute attribute : firstCol) {
			if (attributeNotFoundInCollection(attribute, secondCol)) {
				return false;
			}
		}
		return true;
	}

	private boolean attributeNotFoundInCollection(DataAttribute attribute,
			Collection<DataAttribute> attributes) {
		Predicate<DataAttribute> predicateForNameAndValue = createPredicateForNameAndValue(
				attribute);
		return attributes.stream().noneMatch(predicateForNameAndValue);
	}

	private Predicate<DataAttribute> createPredicateForNameAndValue(DataAttribute attribute) {
		return attributeFromCollection -> attributesAreEqual(attributeFromCollection, attribute);
	}

	private boolean attributesAreEqual(DataAttribute firstAttribute,
			DataAttribute secondAttribute) {
		return secondAttribute.getNameInData().equals(firstAttribute.getNameInData())
				&& secondAttribute.getValue().equals(firstAttribute.getValue());
	}
}
