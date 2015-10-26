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

package se.uu.ub.cora.metadataformat.linkcollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;
import se.uu.ub.cora.metadataformat.metadata.MetadataChildReference;
import se.uu.ub.cora.metadataformat.metadata.MetadataElement;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;

public class DataGroupRecordLinkCollector {

	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String REPEAT_ID = "repeatId";
	private static final String LINKED_PATH = "linkedPath";
	private MetadataHolder metadataHolder;
	private String fromRecordType;
	private String fromRecordId;
	private List<DataGroup> linkList;
	private DataGroup dataGroup;
	private DataGroup totalPath;

	public DataGroupRecordLinkCollector(MetadataHolder metadataHolder, String fromRecordType,
			String fromRecordId) {
		this.metadataHolder = metadataHolder;
		this.fromRecordType = fromRecordType;
		this.fromRecordId = fromRecordId;
	}

	public List<DataGroup> collectLinks(String metadataId, DataGroup dataGroup) {

		this.dataGroup = dataGroup;
		linkList = new ArrayList<>();

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();
		collectLinksFromDataGroupUsingMetadataChildren(metadataChildReferences);

		return linkList;
	}

	private void collectLinksFromDataGroupUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectLinksFromDataGroupUsingMetadataChild(metadataChildReference);
		}
	}

	private void collectLinksFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference) {
		MetadataElement childMetadataElement = metadataHolder
				.getMetadataElement(metadataChildReference.getReferenceId());
		if (isMetadataElementConcerningLinks(childMetadataElement)) {
			findDataGroupAndCollectLinks(childMetadataElement);
		}
	}

	private boolean isMetadataElementConcerningLinks(MetadataElement childMetadataElement) {
		return isDataToDataLink(childMetadataElement)
				|| childMetadataElement instanceof MetadataGroup;
	}

	private void findDataGroupAndCollectLinks(MetadataElement childMetadataElement) {
		for (DataElement childDataElement : dataGroup.getChildren()) {
			collectLinksFromDataGroupChild(childMetadataElement, childDataElement);
		}
	}

	private void collectLinksFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement childDataElement) {
		if (childDataIsSpecifiedByMetadata(childMetadataElement, childDataElement)) {
			createLinkOrParseChildGroup(childMetadataElement, childDataElement);
		}
	}

	private boolean childDataIsSpecifiedByMetadata(MetadataElement childMetadataElement,
			DataElement dataElement) {
		String dataNameInData = dataElement.getNameInData();
		String metadataNameInData = childMetadataElement.getNameInData();
		return dataNameInData.equals(metadataNameInData);
	}

	private void createLinkOrParseChildGroup(MetadataElement childMetadataElement,
			DataElement childDataElement) {
		DataGroup pathCopy = null == totalPath ? null : copyPath(totalPath);
		if (isDataToDataLink(childMetadataElement)) {
			collectRecordToRecordLink((DataToDataLink) childMetadataElement, childDataElement,
					pathCopy);
		} else {
			collectLinksFromSubGroup(childMetadataElement, (DataGroup) childDataElement, pathCopy);
		}
	}

	private DataGroup copyPath(DataGroup pathToCopy) {
		DataGroup pathCopy = DataGroup.withNameInData(LINKED_PATH);
		pathCopy.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA,
				pathToCopy.getFirstAtomicValueWithNameInData(NAME_IN_DATA)));
		copyRepeatId(pathToCopy, pathCopy);
		copyAttributes(pathToCopy, pathCopy);
		copyLinkedPath(pathToCopy, pathCopy);
		return pathCopy;
	}

	private void copyRepeatId(DataGroup pathToCopy, DataGroup pathCopy) {
		if (pathToCopy.containsChildWithNameInData(REPEAT_ID)) {
			pathCopy.addChild(DataAtomic.withNameInDataAndValue(REPEAT_ID,
					pathToCopy.getFirstAtomicValueWithNameInData(REPEAT_ID)));
		}
	}

	private void copyAttributes(DataGroup pathToCopy, DataGroup pathCopy) {
		if (pathToCopy.containsChildWithNameInData(ATTRIBUTES)) {
			DataGroup attributes = DataGroup.withNameInData(ATTRIBUTES);
			pathCopy.addChild(attributes);
			for (DataElement attributeToCopy : pathToCopy.getFirstGroupWithNameInData(ATTRIBUTES)
					.getChildren()) {
				copyAttribute(attributes, attributeToCopy);
			}
		}
	}

	private void copyAttribute(DataGroup attributes, DataElement attributeToCopy) {
		DataGroup attribute = DataGroup.withNameInData(ATTRIBUTE);
		attributes.addChild(attribute);
		for (DataElement attributePart : ((DataGroup) attributeToCopy).getChildren()) {
			attribute.addChild(DataAtomic.withNameInDataAndValue(attributePart.getNameInData(),
					((DataAtomic) attributePart).getValue()));
		}
	}

	private void copyLinkedPath(DataGroup pathToCopy, DataGroup pathCopy) {
		if (pathToCopy.containsChildWithNameInData(LINKED_PATH)) {
			pathCopy.addChild(copyPath(pathToCopy.getFirstGroupWithNameInData(LINKED_PATH)));
		}
	}

	private void collectLinksFromSubGroup(MetadataElement childMetadataElement,
			DataGroup childDataElement, DataGroup pathCopy) {
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder,
				fromRecordType, fromRecordId);

		List<DataGroup> collectedLinks = collector.collectSubLinks(childMetadataElement.getId(),
				childDataElement, pathCopy);
		linkList.addAll(collectedLinks);
	}

	private List<DataGroup> collectSubLinks(String metadataId, DataGroup dataGroup,
			DataGroup parentPath) {

		extendTotalPathWithThisDataGroupsInformation(dataGroup, parentPath);

		return collectLinks(metadataId, dataGroup);
	}

	private void extendTotalPathWithThisDataGroupsInformation(DataGroup dataGroup,
			DataGroup parentPath) {
		DataGroup currentPath = DataGroup.withNameInData(LINKED_PATH);
		currentPath.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, dataGroup.getNameInData()));
		extendPathWithAttributes(dataGroup, currentPath);
		extendPathWithRepeatId(dataGroup, currentPath);
		if (null != parentPath) {
			// find lowest path
			DataGroup lowestPath = findLowestPath(parentPath);
			totalPath = parentPath;
			lowestPath.addChild(currentPath);
		} else {
			totalPath = currentPath;
		}
	}

	private void extendPathWithAttributes(DataGroup dataGroup, DataGroup currentPath) {
		if (!dataGroup.getAttributes().isEmpty()) {
			DataGroup attributes = DataGroup.withNameInData(ATTRIBUTES);
			currentPath.addChild(attributes);
			for (Entry<String, String> entry : dataGroup.getAttributes().entrySet()) {
				DataGroup attribute = DataGroup.withNameInData(ATTRIBUTE);
				attributes.addChild(attribute);
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeName", entry.getKey()));
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeValue", entry.getValue()));
			}
		}
	}

	private void extendPathWithRepeatId(DataGroup dataGroup, DataGroup currentPath) {
		if (dataGroup.getRepeatId() != null) {
			currentPath.addChild(
					DataAtomic.withNameInDataAndValue(REPEAT_ID, dataGroup.getRepeatId()));
		}
	}

	private DataGroup findLowestPath(DataGroup parentPath) {
		if (parentPath.containsChildWithNameInData(LINKED_PATH)) {
			return findLowestPath(parentPath.getFirstGroupWithNameInData(LINKED_PATH));
		}
		return parentPath;
	}

	private boolean isDataToDataLink(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof DataToDataLink;
	}

	private void collectRecordToRecordLink(DataToDataLink recordLink, DataElement dataElement,
			DataGroup parentPath) {
		DataGroup currentPath = DataGroup.withNameInData(LINKED_PATH);
		currentPath.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, dataElement.getNameInData()));

		if (dataElement.getRepeatId() != null) {
			currentPath.addChild(
					DataAtomic.withNameInDataAndValue(REPEAT_ID, dataElement.getRepeatId()));
		}
		if (null != parentPath) {
			// find lowest path
			DataGroup lowestPath = findLowestPath(parentPath);
			totalPath = parentPath;
			lowestPath.addChild(currentPath);
		} else {
			totalPath = currentPath;
		}
		// create link
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");
		linkList.add(recordToRecordLink);
		createFromPart(dataElement, recordToRecordLink);
		createToPart(recordLink, dataElement, recordToRecordLink);
	}

	private void createFromPart(DataElement dataElement, DataGroup recordToRecordLink) {
		DataRecordLink from = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("from",
				fromRecordType, fromRecordId);
		recordToRecordLink.addChild(from);
		from.setLinkedRepeatId(dataElement.getRepeatId());
		from.setLinkedPath(totalPath);
	}

	private void createToPart(DataToDataLink recordLink, DataElement dataElement,
			DataGroup recordToRecordLink) {
		DataRecordLink dataRecordLink = (DataRecordLink) dataElement;
		DataRecordLink to = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("to",
				dataRecordLink.getRecordType(), dataRecordLink.getRecordId());
		recordToRecordLink.addChild(to);
		to.setLinkedPath(recordLink.getLinkedPath());
		to.setLinkedRepeatId(dataRecordLink.getLinkedRepeatId());
	}

}
