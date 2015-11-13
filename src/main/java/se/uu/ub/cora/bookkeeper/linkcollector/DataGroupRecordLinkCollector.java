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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

public class DataGroupRecordLinkCollector {

	private MetadataHolder metadataHolder;
	private String fromRecordType;
	private String fromRecordId;
	private List<DataGroup> linkList;
	private DataGroup dataGroup;
	private DataGroup elementPath;

	public DataGroupRecordLinkCollector(MetadataHolder metadataHolder, String fromRecordType,
			String fromRecordId) {
		this.metadataHolder = metadataHolder;
		this.fromRecordType = fromRecordType;
		this.fromRecordId = fromRecordId;
	}

	public List<DataGroup> collectLinks(String metadataGroupId, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		linkList = new ArrayList<>();

		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectLinksFromDataGroupUsingMetadataChildren(metadataChildReferences);
		return copyLinkList();
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
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
		if (metadataElementConcernsLinks(childMetadataElement)) {
			collectLinksFromDataGroupChildren(childMetadataElement);
		}
	}

	private boolean metadataElementConcernsLinks(MetadataElement childMetadataElement) {
		return isRecordLink(childMetadataElement) || childMetadataElement instanceof MetadataGroup;
	}

	private void collectLinksFromDataGroupChildren(MetadataElement childMetadataElement) {
		for (DataElement childDataElement : dataGroup.getChildren()) {
			collectLinksFromDataGroupChild(childMetadataElement, childDataElement);
		}
	}

	private void collectLinksFromDataGroupChild(MetadataElement childMetadataElement,
			DataElement childDataElement) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			createLinkOrParseChildGroup(childMetadataElement, childDataElement);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataElement dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void createLinkOrParseChildGroup(MetadataElement childMetadataElement,
			DataElement childDataElement) {
		DataGroup childPath = createChildPath(childDataElement);

		if (isRecordLink(childMetadataElement)) {
			createRecordToRecordLink((RecordLink) childMetadataElement, childDataElement,
					childPath);
		} else {
			collectLinksFromSubGroup(childMetadataElement, (DataGroup) childDataElement, childPath);
		}
	}

	private DataGroup createChildPath(DataElement childDataElement) {
		DataGroup pathCopy = PathCopier.copyPath(elementPath);
		return PathExtender.extendPathWithElementInformation(pathCopy, childDataElement);
	}

	private void createRecordToRecordLink(RecordLink recordLink, DataElement dataElement,
			DataGroup fromPath) {
		DataGroup recordToRecordLink = DataGroup.withNameInData("recordToRecordLink");
		recordToRecordLink.addChild(createFromPart(dataElement, fromPath));
		recordToRecordLink.addChild(createToPart((DataRecordLink) dataElement, recordLink));
		linkList.add(recordToRecordLink);
	}

	private DataRecordLink createFromPart(DataElement dataElement, DataGroup fromPath) {
		DataRecordLink from = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"from", fromRecordType, fromRecordId);
		from.setLinkedRepeatId(dataElement.getRepeatId());
		from.setLinkedPath(fromPath);
		return from;
	}

	private DataRecordLink createToPart(DataRecordLink dataRecordLink, RecordLink recordLink) {
		DataRecordLink to = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("to",
				dataRecordLink.getLinkedRecordType(), dataRecordLink.getLinkedRecordId());
		to.setLinkedPath(recordLink.getLinkedPath());
		to.setLinkedRepeatId(dataRecordLink.getLinkedRepeatId());
		return to;
	}

	private void collectLinksFromSubGroup(MetadataElement childMetadataElement, DataGroup subGroup,
			DataGroup pathCopy) {
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder,
				fromRecordType, fromRecordId);

		List<DataGroup> collectedLinks = collector.collectSubLinks(childMetadataElement.getId(),
				subGroup, pathCopy);
		linkList.addAll(collectedLinks);
	}

	private List<DataGroup> collectSubLinks(String metadataId, DataGroup subGroup, DataGroup path) {
		elementPath = path;
		return collectLinks(metadataId, subGroup);
	}

	private boolean isRecordLink(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof RecordLink;
	}

	private List<DataGroup> copyLinkList() {
		List<DataGroup> listOut = new ArrayList<>();
		listOut.addAll(linkList);
		return listOut;
	}
}
