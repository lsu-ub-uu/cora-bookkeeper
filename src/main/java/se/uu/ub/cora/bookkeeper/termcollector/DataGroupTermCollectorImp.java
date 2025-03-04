/*
 * Copyright 2017, 2018, 2019, 2022, 2024, 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermLink;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.collected.IndexTerm;
import se.uu.ub.cora.data.collected.PermissionTerm;
import se.uu.ub.cora.data.collected.StorageTerm;

public class DataGroupTermCollectorImp implements DataGroupTermCollector {

	private MetadataHolder metadataHolder;
	private CollectTermHolder collectTermHolder;

	private CollectTerms collectTerms;

	@Override
	public CollectTerms collectTerms(String metadataGroupId, DataRecordGroup dataRecordGroup) {
		initializeCollectTerms(dataRecordGroup);
		prepareAndCollectTermsFromData(metadataGroupId, dataRecordGroup);

		return collectTerms;
	}

	private void initializeCollectTerms(DataRecordGroup dataRecordGroup) {
		collectTerms = new CollectTerms();
		collectTerms.recordType = extractTypeFromRecord(dataRecordGroup);
		collectTerms.recordId = extractIdFromDataRecord(dataRecordGroup);

	}

	private Optional<String> extractTypeFromRecord(DataRecordGroup dataRecordGroup) {
		try {
			return Optional.of(dataRecordGroup.getType());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private Optional<String> extractIdFromDataRecord(DataRecordGroup dataRecordGroup) {
		try {
			return Optional.of(dataRecordGroup.getId());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private void prepareAndCollectTermsFromData(String metadataGroupId,
			DataRecordGroup dataRecordGroup) {
		if (metadataHolder == null) {
			metadataHolder = MetadataHolderProvider.getHolder();
			populateCollectTermHolderFromMetadataStorage();
		}
		DataGroup dataGroup = DataProvider.createGroupFromRecordGroup(dataRecordGroup);
		collectTermsFromDataUsingMetadata(metadataGroupId, dataGroup);
	}

	private void populateCollectTermHolderFromMetadataStorage() {
		// TODO: possibly create provider for collectTermHolder
		MetadataStorageView metadataStorage = MetadataStorageProvider.getStorageView();
		collectTermHolder = metadataStorage.getCollectTermHolder();
	}

	private void collectTermsFromDataUsingMetadata(String metadataGroupId, DataGroup dataGroup) {
		List<MetadataChildReference> metadataChildReferences = getMetadataGroupChildReferences(
				metadataGroupId);
		collectTermsFromDataUsingMetadataChildren(metadataChildReferences, dataGroup);
	}

	private List<MetadataChildReference> getMetadataGroupChildReferences(String metadataGroupId) {
		MetadataGroup metadataGroup = (MetadataGroup) metadataHolder
				.getMetadataElement(metadataGroupId);
		return metadataGroup.getChildReferences();
	}

	private void collectTermsFromDataUsingMetadataChildren(
			List<MetadataChildReference> metadataChildReferences, DataGroup dataGroup) {
		for (MetadataChildReference metadataChildReference : metadataChildReferences) {
			collectDataForMetadataChildIfItHasAtLeastOneCollectTerm(metadataChildReference,
					dataGroup);
			recurseAndCollectTermsFromChildsGroupChildren(dataGroup, metadataChildReference);
		}
	}

	private void recurseAndCollectTermsFromChildsGroupChildren(DataGroup dataGroup,
			MetadataChildReference metadataChildReference) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		if (isMetadataGroup(childMetadataElement)) {
			recurseAndCollectTermFromChildsGroupChildren(dataGroup, childMetadataElement);
		}
	}

	private void recurseAndCollectTermFromChildsGroupChildren(DataGroup dataGroup,
			MetadataElement childMetadataElement) {
		String childMetadataGroupId = childMetadataElement.getId();
		for (DataChild childDataElement : dataGroup.getChildren()) {
			possiblyCollectTerms(childMetadataElement, childMetadataGroupId, childDataElement);
		}
	}

	private void possiblyCollectTerms(MetadataElement childMetadataElement,
			String childMetadataGroupId, DataChild childDataElement) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			collectTermsFromDataUsingMetadata(childMetadataGroupId, (DataGroup) childDataElement);
		}
	}

	private boolean isMetadataGroup(MetadataElement childMetadataElement) {
		return childMetadataElement instanceof MetadataGroup;
	}

	private void collectDataForMetadataChildIfItHasAtLeastOneCollectTerm(
			MetadataChildReference metadataChildReference, DataGroup dataGroup) {
		if (childReferenceHasCollectTerms(metadataChildReference)) {
			collectTermsFromDataGroupUsingMetadataChild(metadataChildReference, dataGroup);
		}
	}

	private boolean childReferenceHasCollectTerms(MetadataChildReference metadataChildReference) {
		return !metadataChildReference.getCollectTerms().isEmpty();
	}

	private void collectTermsFromDataGroupUsingMetadataChild(
			MetadataChildReference metadataChildReference, DataGroup dataGroup) {
		String referenceId = metadataChildReference.getLinkedRecordId();
		MetadataElement childMetadataElement = metadataHolder.getMetadataElement(referenceId);
		collectTermsFromDataGroupChildren(childMetadataElement,
				metadataChildReference.getCollectTerms(), dataGroup);
	}

	private void collectTermsFromDataGroupChildren(MetadataElement childMetadataElement,
			List<CollectTermLink> collectTermsForChildReference, DataGroup dataGroup) {
		for (DataChild childDataElement : dataGroup.getChildren()) {
			collectTermsFromDataGroupChild(childMetadataElement, childDataElement,
					collectTermsForChildReference);
		}
	}

	private void collectTermsFromDataGroupChild(MetadataElement childMetadataElement,
			DataChild childDataElement, List<CollectTermLink> collectTermsForChildReference) {
		if (childMetadataSpecifiesChildData(childMetadataElement, childDataElement)) {
			collectTermsFromDataGroupChildMatchingMetadata(childMetadataElement, childDataElement,
					collectTermsForChildReference);
		}
	}

	private void collectTermsFromDataGroupChildMatchingMetadata(
			MetadataElement childMetadataElement, DataChild childDataElement,
			List<CollectTermLink> collectTermsForChildReference) {
		if (childMetadataElement instanceof RecordLink) {
			createCollectTermsForRecordLink((DataRecordLink) childDataElement,
					collectTermsForChildReference);
		} else {
			possiblyCreateCollectedTerms(childDataElement, collectTermsForChildReference);
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

	private void createCollectTermsForRecordLink(DataRecordLink childDataElement,
			List<CollectTermLink> collectTerms) {
		String value = createValueForLinkedData(childDataElement);
		for (CollectTermLink collectTerm : collectTerms) {
			createAndAddCollectedTermUsingIdAndValue(collectTerm.id, value);
		}
	}

	private String createValueForLinkedData(DataRecordLink linkGroup) {
		return linkGroup.getLinkedRecordType() + "_" + linkGroup.getLinkedRecordId();
	}

	private void possiblyCreateCollectedTerms(DataChild childDataElement,
			List<CollectTermLink> collectTerms) {
		if (childDataElement instanceof DataAtomic) {
			createCollectTerms(childDataElement, collectTerms);
		}
	}

	private void createCollectTerms(DataChild childDataElement,
			List<CollectTermLink> collectTerms) {
		for (CollectTermLink collectTerm : collectTerms) {
			String childDataElementValue = ((DataAtomic) childDataElement).getValue();
			createAndAddCollectedTermUsingIdAndValue(collectTerm.id, childDataElementValue);
		}
	}

	private void createAndAddCollectedTermUsingIdAndValue(String collectTermId, String value) {
		CollectTerm collectTerm = collectTermHolder.getCollectTermById(collectTermId);
		buildCollectTerm(collectTerm, value);
	}

	private void buildCollectTerm(CollectTerm collectTerm, String value) {
		switch (collectTerm) {
		case se.uu.ub.cora.bookkeeper.metadata.PermissionTerm term: {
			PermissionTerm permissionTerm = buildPermissionTerm(term, value);
			collectTerms.addPermissionTerm(permissionTerm);
			break;
		}
		case se.uu.ub.cora.bookkeeper.metadata.StorageTerm term: {
			StorageTerm storageTerm = buildStorageTerm(term, value);
			collectTerms.addStorageTerm(storageTerm);
			break;
		}
		case se.uu.ub.cora.bookkeeper.metadata.IndexTerm term: {
			IndexTerm indexTerm = buildIndexTerm(term, value);
			collectTerms.addIndexTerm(indexTerm);
			break;
		}
		default:
			break;
		}
	}

	private PermissionTerm buildPermissionTerm(
			se.uu.ub.cora.bookkeeper.metadata.PermissionTerm term, String value) {
		return new PermissionTerm(term.id, value, term.permissionKey);
	}

	private StorageTerm buildStorageTerm(se.uu.ub.cora.bookkeeper.metadata.StorageTerm term,
			String value) {
		return new StorageTerm(term.id, term.storageKey, value);
	}

	private IndexTerm buildIndexTerm(se.uu.ub.cora.bookkeeper.metadata.IndexTerm term,
			String value) {
		return new IndexTerm(term.id, value, term.indexFieldName, term.indexType);
	}

}
