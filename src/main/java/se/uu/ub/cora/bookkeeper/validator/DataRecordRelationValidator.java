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

package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordRelation;

public class DataRecordRelationValidator implements DataElementValidator {

	private DataValidatorFactoryImp dataValidatorFactoryImp;
	private MetadataHolder metadataHolder;
	private RecordRelation recordRelation;
	protected DataGroup dataGroup;
	protected ValidationAnswer validationAnswer;


	public DataRecordRelationValidator(DataValidatorFactoryImp dataValidatorFactoryImp,
			MetadataHolder metadataHolder, RecordRelation recordRelation) {
		this.dataValidatorFactoryImp = dataValidatorFactoryImp;
		this.metadataHolder = metadataHolder;
		this.recordRelation = recordRelation;
//		super(dataValidatorFactoryImp, metadataHolder, metadataGroup);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ValidationAnswer validateData(DataElement dataGroup) {
		this.dataGroup = (DataGroup) dataGroup;
		validationAnswer = new ValidationAnswer();
		validateNameInData();
		validateRefRecordLinkId();
		validateRefMetadataGroupId();
		return validationAnswer;
	}

	private void validateNameInData() {
		String metadataNameInData = recordRelation.getNameInData();
		String dataNameInData = dataGroup.getNameInData();
		if (!metadataNameInData.equals(dataNameInData)) {
			validationAnswer.addErrorMessage("DataGroup should have name(nameInData): "
					+ recordRelation.getNameInData() + " it does not.");
		}
	}

	private void validateRefRecordLinkId() {
		if (!dataGroup.containsChildWithNameInData("refRecordLinkId")
				|| dataGroup.getFirstAtomicValueWithNameInData("refRecordLinkId").isEmpty()) {
			validationAnswer.addErrorMessage(
					"DataRecordRelation with nameInData:" + dataGroup.getNameInData() + " must have an nonempty refRecordLinkId as child.");
		}
	}

	private void validateRefMetadataGroupId() {
		if(!dataGroup.containsChildWithNameInData("refMetadataGroupId")
				|| dataGroup.getFirstAtomicValueWithNameInData("refMetadataGroupId").isEmpty()){
			validationAnswer.addErrorMessage("DataRecordRelation with nameInData:" + dataGroup.getNameInData() + " must have an nonempty refMetadataGroupId as child.");
		}
	}
}
