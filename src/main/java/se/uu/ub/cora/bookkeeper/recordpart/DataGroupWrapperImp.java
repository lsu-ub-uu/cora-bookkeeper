/*
 * Copyright 2020, 2022 Uppsala University Library
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperImp implements DataGroup, DataGroupWrapper {
	Map<String, List<List<DataAttribute>>> removedElements = new HashMap<>();
	Map<Class, Set<String>> removedTypes = new HashMap<>();
	Class<? extends DataChild> removedType;
	String removedName;
	DataGroup dataGroup;
	private List<DataChildFilter> childFiltersCalledForRemove = new ArrayList<>();
	private TypeInfo typeInfo;

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
	public void addChild(DataChild dataElement) {
		dataGroup.addChild(dataElement);
	}

	@Override
	public void addChildren(Collection<DataChild> dataElements) {
		dataGroup.addChildren(dataElements);
	}

	@Override
	public List<DataChild> getChildren() {
		return dataGroup.getChildren();
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		return dataGroup.getAllChildrenWithNameInData(nameInData);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		return dataGroup.getAllChildrenWithNameInDataAndAttributes(nameInData, childAttributes);
	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
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
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		return dataGroup.getAllDataAtomicsWithNameInDataAndAttributes(childNameInData,
				childAttributes);
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
	public boolean hasRemovedBeenCalled(DataChild child) {
		// Set<String> set = removedTypes.get(child.getClass().getSimpleName());
		// Class next = removedTypes.keySet().iterator().next();
		// if (child instanceof next) {
		//
		// }
		// if (set.contains(child.getNameInData())) {
		// return true;
		// }
		// if (class1.isInstance(removedType)) {
		if (null != typeInfo && typeInfo.type.isInstance(child)
				&& typeInfo.name.equals(child.getNameInData())) {
			return true;
		}
		// if (removedType.isInstance(child)) {
		// return true;
		// }

		for (DataChildFilter dataChildFilter : childFiltersCalledForRemove) {
			if (dataChildFilter.childMatches(child)) {
				return true;
			}
		}
		if (removeHasNotBeenCalledForNameInData(child)) {
			return false;
		}
		return removeHasBeenCalledForChildsAttributes(child);
	}

	private boolean removeHasNotBeenCalledForNameInData(DataChild child) {
		return !removedElements.containsKey(child.getNameInData());
	}

	private boolean removeHasBeenCalledForChildsAttributes(DataChild child) {
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

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		dataGroup.addAttributeByIdWithValue(nameInData, value);
	}

	@Override
	public boolean hasAttributes() {
		return dataGroup.hasAttributes();
	}

	@Override
	public DataAttribute getAttribute(String nameInData) {
		return dataGroup.getAttribute(nameInData);
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return dataGroup.getAttributes();
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		return dataGroup.getAllChildrenMatchingFilter(childFilter);
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		childFiltersCalledForRemove.add(childFilter);
		return dataGroup.removeAllChildrenMatchingFilter(childFilter);
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> type, String name) {
		return dataGroup.containsChildOfTypeAndName(type, name);
	}

	@Override
	public <T extends DataChild> T getFirstChildOfTypeAndName(Class<T> type, String name) {
		return dataGroup.getFirstChildOfTypeAndName(type, name);
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfTypeAndName(Class<T> type, String name) {
		return dataGroup.getChildrenOfTypeAndName(type, name);
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> type,
			String name) {
		// removedTypes.put(type, Set.of(name));
		removedType = type;
		removedName = name;
		typeInfo = new TypeInfo(type, name);
		return dataGroup.removeFirstChildWithTypeAndName(type, name);
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	private record TypeInfo(Class<? extends DataChild> type, String name) {
	}
}
