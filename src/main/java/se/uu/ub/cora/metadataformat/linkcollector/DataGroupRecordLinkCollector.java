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
			// TODO: go through subGroup
			DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(
					metadataHolder, fromRecordType, fromRecordId);

			List<DataGroup> collectedLinks = collector.collectSubLinks(childMetadataElement.getId(),
					(DataGroup) childDataElement, pathCopy);
			linkList.addAll(collectedLinks);
		}
	}

	private DataGroup copyPath(DataGroup pathToCopy) {
		DataGroup pathCopy = DataGroup.withNameInData("linkedPath");
		pathCopy.addChild(DataAtomic.withNameInDataAndValue("nameInData",
				pathToCopy.getFirstAtomicValueWithNameInData("nameInData")));
		if (pathToCopy.containsChildWithNameInData("repeatId")) {
			pathCopy.addChild(DataAtomic.withNameInDataAndValue("repeatId",
					pathToCopy.getFirstAtomicValueWithNameInData("repeatId")));
		}
		if (pathToCopy.containsChildWithNameInData("attributes")) {
			DataGroup attributes = DataGroup.withNameInData("attributes");
			pathCopy.addChild(attributes);
			for (DataElement attributeToCopy : pathToCopy.getFirstGroupWithNameInData("attributes")
					.getChildren()) {
				DataGroup attribute = DataGroup.withNameInData("attribute");
				for (DataElement attributePart : ((DataGroup) attributeToCopy).getChildren()) {

					attribute.addChild(
							DataAtomic.withNameInDataAndValue(attributePart.getNameInData(),
									((DataAtomic) attributePart).getValue()));
				}
			}
		}
		if (pathToCopy.containsChildWithNameInData("linkedPath")) {
			pathCopy.addChild(copyPath(pathToCopy.getFirstGroupWithNameInData("linkedPath")));
		}
		return pathCopy;
	}

	private List<DataGroup> collectSubLinks(String metadataId, DataGroup dataGroup,
			DataGroup parentPath) {

		DataGroup currentPath = DataGroup.withNameInData("linkedPath");
		currentPath.addChild(
				DataAtomic.withNameInDataAndValue("nameInData", dataGroup.getNameInData()));
		if (!dataGroup.getAttributes().isEmpty()) {
			DataGroup attributes = DataGroup.withNameInData("attributes");
			currentPath.addChild(attributes);
			for (Entry<String, String> entry : dataGroup.getAttributes().entrySet()) {
				DataGroup attribute = DataGroup.withNameInData("attribute");
				attributes.addChild(attribute);
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeName", entry.getKey()));
				attribute.addChild(
						DataAtomic.withNameInDataAndValue("attributeValue", entry.getValue()));
			}
		}
		if (dataGroup.getRepeatId() != null) {
			currentPath.addChild(
					DataAtomic.withNameInDataAndValue("repeatId", dataGroup.getRepeatId()));
		}
		if (null != parentPath) {
			// find lowest path
			DataGroup lowestPath = findLowestPath(parentPath);
			totalPath = parentPath;
			lowestPath.addChild(currentPath);
		} else {
			totalPath = currentPath;
		}

		this.dataGroup = dataGroup;
		linkList = new ArrayList<>();

		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder.getMetadataElement(metadataId);
		List<MetadataChildReference> metadataChildReferences = metadataGroup.getChildReferences();
		collectLinksFromDataGroupUsingMetadataChildren(metadataChildReferences);

		return linkList;
	}

	private DataGroup findLowestPath(DataGroup parentPath) {
		if (parentPath.containsChildWithNameInData("linkedPath")) {
			return findLowestPath(parentPath.getFirstGroupWithNameInData("linkedPath"));
		}
		return parentPath;
	}

	private boolean isDataToDataLink(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof DataToDataLink;
	}

	private void collectRecordToRecordLink(DataToDataLink recordLink, DataElement dataElement,
			DataGroup parentPath) {
		DataGroup currentPath = DataGroup.withNameInData("linkedPath");
		currentPath.addChild(
				DataAtomic.withNameInDataAndValue("nameInData", dataElement.getNameInData()));

		if (dataElement.getRepeatId() != null) {
			currentPath.addChild(
					DataAtomic.withNameInDataAndValue("repeatId", dataElement.getRepeatId()));
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
