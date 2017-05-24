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

package se.uu.ub.cora.bookkeeper.data;

import java.util.*;
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

	private String possiblyReturnAtomicChildWithNameInData(String childNameInData,
			Optional<DataAtomic> optionalFirst) {
		if (optionalFirst.isPresent()) {
			return getAtomicValueFromOptional(optionalFirst);
		}
		throw new DataMissingException(
				"Atomic value not found for childNameInData:" + childNameInData);
	}

	private String getAtomicValueFromOptional(Optional<DataAtomic> optionalFirst) {
		return optionalFirst.get().getValue();
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataAtomic.class::cast);
	}

	private Stream<DataElement> getAtomicChildrenStream() {
		return getChildrenStream().filter(isDataAtomic);
	}

	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		Optional<DataGroup> findFirst = getGroupChildrenWithNameInData(childNameInData).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	private Stream<DataGroup> getGroupChildrenWithNameInData(String childNameInData) {
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
		return getGroupChildrenWithNameInData(childNameInData).collect(Collectors.toList());
	}

	public Collection<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return getDataAtomicChildrenWithNameInData(childNameInData).collect(Collectors.toList());
	}

	private Stream<DataAtomic> getDataAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataAtomic.class::cast);
	}
}
