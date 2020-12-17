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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperImp implements DataGroup, DataGroupWrapper {
	Map<String, List<List<DataAttribute>>> removedNameInDatas = new HashMap<>();
	DataGroup dataGroup;

	public DataGroupWrapperImp(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		ensurePlaceForNameInDataExistsInMap(childNameInData);
		removedNameInDatas.get(childNameInData).add(Arrays.asList(childAttributes));
		return dataGroup.removeAllChildrenWithNameInDataAndAttributes(childNameInData,
				childAttributes);
	}

	private void ensurePlaceForNameInDataExistsInMap(String childNameInData) {
		if (!removedNameInDatas.containsKey(childNameInData)) {
			removedNameInDatas.put(childNameInData, new ArrayList<>());
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
		return dataGroup.removeFirstChildWithNameInData(childNameInData);
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		return dataGroup.removeAllChildrenWithNameInData(childNameInData);
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		return dataGroup.getFirstDataAtomicWithNameInData(childNameInData);
	}

	@Override
	public Map<String, List<List<DataAttribute>>> getRemovedNameInDatas() {
		return removedNameInDatas;
	}

	DataGroup getDataGroup() {
		return dataGroup;
	}

	@Override
	public boolean hasRemovedBeenCalledFor(DataElement child) {
		// TODO Auto-generated method stub
		return false;
	}
	// private boolean childIsReplaced(Map<String, List<List<DataAttribute>>> replacedNameInDatas,
	// DataElement updatedChild, String metadataNameInData) {
	//
	// // updatedChild.getNameInData();
	// // updatedChild.getAttributes()
	// if (replacedNameInDatas.containsKey(metadataNameInData)) {
	// return checkAttributesMatch(replacedNameInDatas, updatedChild, metadataNameInData);
	// }
	// return false;
	//
	// }
	//
	// private boolean checkAttributesMatch(Map<String, List<List<DataAttribute>>>
	// replacedNameInDatas,
	// DataElement updatedChild, String metadataNameInData) {
	// List<List<DataAttribute>> alreadyReplacedChildAttributes = replacedNameInDatas
	// .get(metadataNameInData);
	// Collection<DataAttribute> updatedChildAttributes = updatedChild.getAttributes();
	// return attributesExistsOnReplacedChildAttributes(updatedChildAttributes,
	// alreadyReplacedChildAttributes);
	// }
	//
	// private DataElement getMatchingData(DataGroup dataGroup, MetadataGroup metadataGroup) {
	// String metadataNameInData = metadataGroup.getNameInData();
	//
	// List<DataElement> allChildrenWithNameInData = dataGroup
	// .getAllChildrenWithNameInData(metadataNameInData);
	//
	// for (DataElement childDataElement : allChildrenWithNameInData) {
	// MetadataMatchData matcher = matchFactory.factor();
	// ValidationAnswer answer = matcher.metadataSpecifiesData(metadataGroup,
	// childDataElement);
	// if (answer.dataIsValid()) {
	// return childDataElement;
	// }
	// }
	// return null;
	// }
	//
	// private boolean attributesExistsOnReplacedChildAttributes(
	// Collection<DataAttribute> updatedChildAttributes,
	// List<List<DataAttribute>> alreadyReplacedChildAttributes) {
	// for (List<DataAttribute> replacedAttributes : alreadyReplacedChildAttributes) {
	// if (attributesInListMatch(updatedChildAttributes, replacedAttributes)) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private boolean attributesInListMatch(Collection<DataAttribute> attributesInChildToCompare,
	// List<DataAttribute> replacedAttributes) {
	// if (haveDifferentSizes(attributesInChildToCompare, replacedAttributes)) {
	// return false;
	// }
	// return findAnyMatchOnAttributes(attributesInChildToCompare, replacedAttributes);
	//
	// }
	//
	// private boolean haveDifferentSizes(Collection<DataAttribute> attributesInChildToCompare,
	// List<DataAttribute> replacedAttributes) {
	// return attributesInChildToCompare.size() != replacedAttributes.size();
	// }
	//
	// private boolean findAnyMatchOnAttributes(Collection<DataAttribute>
	// attributesInChildToCompare,
	// List<DataAttribute> replacedAttributes) {
	// for (DataAttribute childAttribute : attributesInChildToCompare) {
	// if (matchNotFound(replacedAttributes, childAttribute)) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// private boolean matchNotFound(List<DataAttribute> replacedAttributes,
	// DataAttribute childAttribute) {
	// return replacedAttributes.stream().noneMatch(filterByNameAndValue(childAttribute));
	// }
	//
	// private Predicate<DataAttribute> filterByNameAndValue(DataAttribute childAttribute) {
	// return replacedAttribute -> dataAttributeIsSame(replacedAttribute, childAttribute);
	// }
	//
	// private boolean dataAttributeIsSame(DataAttribute replacedAttribute,
	// DataAttribute childAttribute) {
	// return childAttribute.getNameInData().equals(replacedAttribute.getNameInData())
	// && childAttribute.getValue().equals(replacedAttribute.getValue());
	// }
}
