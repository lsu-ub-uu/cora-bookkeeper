/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DataGroup implements DataPart, DataElement, Data {

	private final String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElement> children = new ArrayList<>();
	private String repeatId;
	private Predicate<DataElement> isDataAtomic = dataElement -> dataElement instanceof DataAtomic;
	private Predicate<DataElement> isDataGroup = dataElement -> dataElement instanceof DataGroup;

	public static DataGroup withNameInData(String nameInData) {
		return new DataGroup(nameInData);
	}

	private DataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public void addAttributeByIdWithValue(String attributeId, String attributeValue) {
		attributes.put(attributeId, attributeValue);
	}

	public String getAttribute(String attributeId) {
		return attributes.get(attributeId);
	}

	public void addChild(DataElement child) {
		children.add(child);
	}

	public List<DataElement> getChildren() {
		return children;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public boolean containsChildWithNameInData(String childNameInData) {
		return getChildrenStream().anyMatch(filterByNameInData(childNameInData));
	}

	private Stream<DataElement> getChildrenStream() {
		return children.stream();
	}

	private Predicate<DataElement> filterByNameInData(String childNameInData) {
		return dataElement -> dataElementsNameInDataIs(dataElement, childNameInData);
	}

	private boolean dataElementsNameInDataIs(DataElement dataElement, String childNameInData) {
		return dataElement.getNameInData().equals(childNameInData);
	}

	public String getFirstAtomicValueWithNameInData(String childNameInData) {
		Optional<DataAtomic> optionalFirst = getAtomicChildrenWithNameInData(childNameInData)
				.findFirst();
		return possiblyReturnAtomicChildWithNameInData(childNameInData, optionalFirst);
	}

	public String getFirstAtomicValueWithNameInDataOrDefault(String childNameInData,
			String defaultValue) {
		Optional<DataAtomic> optionalFirst = getAtomicChildrenWithNameInData(childNameInData)
				.findFirst();
		if (optionalFirst.isPresent()) {
			return optionalFirst.get().getValue();
		}
		return defaultValue;
	}

	private String possiblyReturnAtomicChildWithNameInData(String childNameInData,
			Optional<DataAtomic> optionalFirst) {
		if (optionalFirst.isPresent()) {
			return optionalFirst.get().getValue();
		}
		throw new DataMissingException(
				"Atomic value not found for childNameInData:" + childNameInData);
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataAtomic.class::cast);
	}

	private Stream<DataElement> getAtomicChildrenStream() {
		return getChildrenStream().filter(isDataAtomic);
	}

	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		Optional<DataGroup> findFirst = getGroupChildrenWithNameInDataStream(childNameInData)
				.findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataStream(String childNameInData) {
		return getGroupChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataGroup.class::cast);
	}

	private Stream<DataElement> getGroupChildrenStream() {
		return getChildrenStream().filter(isDataGroup);
	}

	public DataElement getFirstChildWithNameInData(String childNameInData) {
		Optional<DataElement> optionalFirst = possiblyFindFirstChildWithNameInData(childNameInData);
		if (optionalFirst.isPresent()) {
			return optionalFirst.get();
		}
		throw new DataMissingException("Element not found for childNameInData:" + childNameInData);
	}

	private Optional<DataElement> possiblyFindFirstChildWithNameInData(String childNameInData) {
		return getChildrenStream().filter(filterByNameInData(childNameInData)).findFirst();
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	public void removeFirstChildWithNameInData(String childNameInData) {
		boolean childRemoved = tryToRemoveChild(childNameInData);
		if (!childRemoved) {
			throw new DataMissingException(
					"Element not found for childNameInData:" + childNameInData);
		}
	}

	private boolean tryToRemoveChild(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElementsNameInDataIs(dataElement, childNameInData)) {
				getChildren().remove(dataElement);
				return true;
			}
		}
		return false;
	}

	public List<DataGroup> getAllGroupsWithNameInData(String childNameInData) {
		return getGroupChildrenWithNameInDataStream(childNameInData).collect(Collectors.toList());
	}

	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return getDataAtomicChildrenWithNameInData(childNameInData).collect(Collectors.toList());
	}

	private Stream<DataAtomic> getDataAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataAtomic.class::cast);
	}

	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
				.collect(Collectors.toList());
	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataStream(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	private Predicate<DataElement> filterByAttributes(DataAttribute... childAttributes) {
		return dataElement -> dataElementsHasAttributes(dataElement, childAttributes);
	}

	private boolean dataElementsHasAttributes(DataElement dataElement,
			DataAttribute[] childAttributes) {
		Map<String, String> attributesFromElement = dataElement.getAttributes();
		if (diffrentNumberOfAttributesInRequestedAndExisting(childAttributes,
				attributesFromElement)) {
			return false;
		}
		return allRequestedAttributesMatchExistingAttributes(childAttributes,
				attributesFromElement);
	}

	private boolean diffrentNumberOfAttributesInRequestedAndExisting(
			DataAttribute[] childAttributes, Map<String, String> attributesFromElement) {
		return childAttributes.length != attributesFromElement.size();
	}

	private boolean allRequestedAttributesMatchExistingAttributes(DataAttribute[] childAttributes,
			Map<String, String> attributesFromElement) {
		for (DataAttribute dataAttribute : childAttributes) {
			if (attributesDoesNotMatch(attributesFromElement, dataAttribute)) {
				return false;
			}
		}
		return true;
	}

	private boolean attributesDoesNotMatch(Map<String, String> attributesFromElement,
			DataAttribute dataAttribute) {
		return requestedAttributeDoesNotExists(attributesFromElement, dataAttribute)
				|| requestedAttributeHasDifferentValueAsExisting(attributesFromElement,
						dataAttribute);
	}

	private boolean requestedAttributeDoesNotExists(Map<String, String> attributesFromElement,
			DataAttribute dataAttribute) {
		return !attributesFromElement.containsKey(dataAttribute.getNameInData());
	}

	private boolean requestedAttributeHasDifferentValueAsExisting(
			Map<String, String> attributesFromElement, DataAttribute dataAttribute) {
		return !attributesFromElement.get(dataAttribute.getNameInData())
				.equals(dataAttribute.getValue());
	}

	public static DataGroup asLinkWithNameInDataAndTypeAndId(String nameInData, String type,
			String id) {
		DataGroup dataGroup = new DataGroup(nameInData);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", type));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", id));
		return dataGroup;
	}

}
