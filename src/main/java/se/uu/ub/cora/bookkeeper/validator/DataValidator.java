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

package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.data.DataGroup;

/**
 * DataValidator defines the methods used for validating data according to how it is defined in
 * metadata.
 */
public interface DataValidator {
	/**
	 * ValidateData validates the given DataGroup against the metadata specified by the
	 * metadataGroupId. The result of the validation is returned in a ValidationAnswer. If the
	 * validation finds any errors SHOULD as many as possible be returned as part of the answer.
	 * 
	 * @param metadataGroupId
	 *            A String with the id of the metadataGroup to validate against
	 * @param dataGroup
	 *            A DataGroup to validate against the specified metadata
	 * @return A ValidationAnswer with the result of the validation
	 */
	ValidationAnswer validateData(String metadataGroupId, DataGroup dataGroup);
}
