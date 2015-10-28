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

package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.Map.Entry;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public final class PathExtender {
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String REPEAT_ID = "repeatId";
	private static final String LINKED_PATH = "linkedPath";

	private PathExtender() throws InstantiationException {
		throw new InstantiationException("Instances of this class are forbidden");
	}

	public static DataGroup extendPathWithElementInformation(DataGroup path,
			DataElement dataElement) {
		DataGroup elementPath = createPathForDataElement(dataElement);
		return extendPathWithElementPath(path, elementPath);
	}

	private static DataGroup createPathForDataElement(DataElement dataElement) {
		DataGroup elementPath = DataGroup.withNameInData(LINKED_PATH);
		elementPath.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, dataElement.getNameInData()));
		extendPathWithAttributes(dataElement, elementPath);
		extendPathWithRepeatId(dataElement, elementPath);
		return elementPath;
	}

	private static void extendPathWithAttributes(DataElement dataElement, DataGroup currentPath) {
		if (dataElement instanceof DataGroup) {
			DataGroup subGroup = (DataGroup) dataElement;
			addAttributesIfExist(currentPath, subGroup);
		}
	}

	private static void addAttributesIfExist(DataGroup currentPath, DataGroup subGroup) {
		if (!subGroup.getAttributes().isEmpty()) {

			DataGroup attributes = DataGroup.withNameInData(ATTRIBUTES);
			currentPath.addChild(attributes);
			for (Entry<String, String> entry : subGroup.getAttributes().entrySet()) {
				DataGroup attribute = DataGroup.withNameInData(ATTRIBUTE);
				attributes.addChild(attribute);
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeName", entry.getKey()));
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeValue", entry.getValue()));
			}
		}
	}

	private static void extendPathWithRepeatId(DataElement dataElement, DataGroup currentPath) {
		if (dataElement.getRepeatId() != null) {
			currentPath.addChild(
					DataAtomic.withNameInDataAndValue(REPEAT_ID, dataElement.getRepeatId()));
		}
	}

	private static DataGroup extendPathWithElementPath(DataGroup parentPath,
			DataGroup elementPath) {
		if (null == parentPath) {
			return elementPath;
		}
		DataGroup lowestPath = findLowestPath(parentPath);
		lowestPath.addChild(elementPath);
		return parentPath;
	}

	private static DataGroup findLowestPath(DataGroup parentPath) {
		if (parentPath.containsChildWithNameInData(LINKED_PATH)) {
			return findLowestPath(parentPath.getFirstGroupWithNameInData(LINKED_PATH));
		}
		return parentPath;
	}
}
