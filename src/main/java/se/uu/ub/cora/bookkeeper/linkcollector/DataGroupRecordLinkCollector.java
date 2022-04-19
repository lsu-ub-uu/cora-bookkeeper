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

package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;

public class DataGroupRecordLinkCollector {

	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
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
		String referenceId = metadataChildReference.getLinkedRecordId();

		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		if (metadataElementConcernsLinks(childMetadataElement)) {
			collectLinksFromDataGroupChildren(childMetadataElement);
		}
	}

	private boolean metadataElementConcernsLinks(MetadataElement childMetadataElement) {
		return isRecordLink(childMetadataElement) || isMetadataGroup(childMetadataElement);
	}

	private boolean isRecordLink(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof RecordLink;
	}

	private boolean isMetadataGroup(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof MetadataGroup;
	}

	private void collectLinksFromDataGroupChildren(MetadataElement childMetadataElement) {
		for (DataChild childDataElement : dataGroup.getChildren()) {
			collectLinksFromDataGroupChild(childMetadataElement, childDataElement);
		}
	}

	private void collectLinksFromDataGroupChild(MetadataElement childMetadataElement,
			DataChild childDataElement) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			createLinkOrParseChildGroup(childMetadataElement, childDataElement);
		}
	}

	private boolean childMetadataSpecifiesChildData(MetadataElement childMetadataElement,
			DataChild dataElement) {
		MetadataMatchData metadataMatchData = MetadataMatchDataImp
				.withMetadataHolder(metadataHolder);
		ValidationAnswer validationAnswer = metadataMatchData
				.metadataSpecifiesData(childMetadataElement, dataElement);
		return validationAnswer.dataIsValid();
	}

	private void createLinkOrParseChildGroup(MetadataElement childMetadataElement,
			DataChild childDataElement) {
		DataGroup childPath = createChildPath(childDataElement);

		if (isRecordLink(childMetadataElement)) {
			createRecordToRecordLink((RecordLink) childMetadataElement,
					(DataGroup) childDataElement, childPath);
		} else {
			collectLinksFromSubGroup(childMetadataElement, (DataGroup) childDataElement, childPath);
		}
	}

	private DataGroup createChildPath(DataChild childDataElement) {
		DataGroup pathCopy = PathCopier.copyPath(elementPath);
		return PathExtender.extendPathWithElementInformation(pathCopy, childDataElement);
	}

	private void createRecordToRecordLink(RecordLink recordLink, DataGroup dataElement,
			DataGroup fromPath) {
		DataGroup recordToRecordLink = DataGroupProvider
				.getDataGroupUsingNameInData("recordToRecordLink");
		recordToRecordLink.addChild(createFromPart(dataElement, fromPath));
		recordToRecordLink.addChild(createToPart(dataElement, recordLink));
		linkList.add(recordToRecordLink);
	}

	private DataRecordLink createFromPart(DataChild dataElement, DataGroup fromPath) {
		DataRecordLink from = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId("from",
				fromRecordType, fromRecordId);
		return from;
	}

	private DataRecordLink createToPart(DataGroup dataElement, RecordLink recordLink) {
		String type = dataElement.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		String id = dataElement.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		DataRecordLink to = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId("to", type,
				id);
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

	private List<DataGroup> copyLinkList() {
		List<DataGroup> listOut = new ArrayList<>();
		listOut.addAll(linkList);
		return listOut;
	}
}
