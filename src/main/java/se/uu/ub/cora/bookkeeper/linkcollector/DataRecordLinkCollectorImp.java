/*
 * Copyright 2015, 2017, 2019, 2025 Uppsala University Library
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataParent;
import se.uu.ub.cora.data.collected.Link;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private MetadataHolder metadataHolder;
	private Set<Link> linkSet;

	@Override
	public Set<Link> collectLinks(String metadataId, DataGroup dataGroup) {
		getMetadataFromStorage();
		linkSet = new LinkedHashSet<>();
		collectLinksForCurrentLevel(metadataId, dataGroup);
		return linkSet;
	}

	private void getMetadataFromStorage() {
		metadataHolder = MetadataHolderProvider.getHolder();
	}

	public void collectLinksForCurrentLevel(String metadataGroupId, DataGroup dataGroup) {
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectLinksFromDataGroupUsingMetadataChildren(metadataChildReferences, dataGroup);
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectLinksFromDataGroupUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences, DataParent dataGroup) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectLinksFromDataGroupUsingMetadataChild(metadataChildReference, dataGroup);
		}
	}

	private void collectLinksFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference, DataParent dataGroup) {
		String referenceId = metadataChildReference.getLinkedRecordId();

		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		if (metadataElementConcernsLinks(childMetadataElement)) {
			collectLinksFromDataGroupChildren(childMetadataElement, dataGroup);
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

	private void collectLinksFromDataGroupChildren(MetadataElement childMetadataElement,
			DataParent dataGroup) {
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

		if (isRecordLink(childMetadataElement)) {
			createRecordToRecordLink((DataGroup) childDataElement);
		} else {
			collectLinksFromSubGroup(childMetadataElement, (DataGroup) childDataElement);
		}
	}

	private void createRecordToRecordLink(DataGroup dataElement) {
		linkSet.add(createToPart(dataElement));
	}

	private Link createToPart(DataGroup dataElement) {
		String type = dataElement.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		String id = dataElement.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		return new Link(type, id);
	}

	private void collectLinksFromSubGroup(MetadataElement childMetadataElement,
			DataGroup subGroup) {
		collectLinksForCurrentLevel(childMetadataElement.getId(), subGroup);
	}
}
