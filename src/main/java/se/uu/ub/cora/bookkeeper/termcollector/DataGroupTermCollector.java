/*
 * Copyright 2017, 2019 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collectterms.CollectTerms;

/**
 * DataGroupTermCollector collects terms specified in metadata for a DataGroup.
 * <p>
 * There are three different kinds of collect terms, permission, storage and index.
 */
public interface DataGroupTermCollector {
	/**
	 * collectTerms is used to extract collect terms from a dataGroup based on the meta information
	 * found in the metadataGroup identified by the provided metadataGroupId.
	 * 
	 * @param metadataGroupId
	 *            A String with the id of the metadataGroup that describes the metadata for the
	 *            entered DataGroup
	 * @param dataGroup
	 *            A DataGroup to extract collect terms from
	 * @return A DataGroup with the extracted collect terms. The DataGroup has collectedData as
	 *         nameInData and an Atomic child, type, with the dataGroups type, and another Atomic
	 *         child, id, with the DataGroups id.
	 *         <p>
	 *         The collected terms are added in sub DataGroups with nameInData, permission, storage,
	 *         and index respectively.
	 */
	CollectTerms collectTerms(String metadataGroupId, DataGroup dataGroup);

	/**
	 * collectTermsWithoutTypeAndId is used to extract collect terms from a dataGroup based on the
	 * meta information found in the metadataGroup identified by the provided metadataGroupId
	 * ignoring Type and Id. This method is similar to {@link #collectTerms(String, DataGroup)} but
	 * ignoring Type and Id.
	 * 
	 * @param metadataGroupId
	 *            A String with the id of the metadataGroup that describes the metadata for the
	 *            entered DataGroup
	 * @param dataGroup
	 *            A DataGroup to extract collect terms from
	 * @return A DataGroup with the extracted collect terms. The DataGroup has collectedData as
	 *         nameInData.
	 *         <p>
	 *         The collected terms are added in sub DataGroups with nameInData, permission, storage,
	 *         and index respectively.
	 */
	DataGroup collectTermsWithoutTypeAndId(String metadataGroupId, DataGroup dataGroup);
}
