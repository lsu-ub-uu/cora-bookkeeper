/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.decorator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;

/**
 * DataDecarator defines the methods used for decorating data according to how it is defined in
 * metadata.
 */
public interface DataDecarator {
	/**
	 * DecorateData decorates the given DataGroup with texts from the metadata specified by the
	 * metadataGroupId.
	 * <p>
	 * if something in the decoration goes wrong, SHOULD an {@link DataDecaratorException} be thrown
	 * 
	 * @param metadataGroupId
	 *            A String with the id of the metadataGroup to use to decorate
	 * @throws DataDecaratorException
	 *             if the element does not exist
	 * @param dataGroup
	 *            A DataGroup to decorate using the specified metadata
	 */
	void decorateDataGroup(String metadataGroupId, DataGroup dataGroup);

	/**
	 * decorateRecord decorates the given DataRecord with texts from the metadata specified by the
	 * metadataGroupId.
	 * <p>
	 * if something in the decoration goes wrong, SHOULD an {@link DataDecaratorException} be thrown
	 * 
	 * @param metadataGroupId
	 *            A String with the id of the metadataGroup to use to decorate
	 * @param dataRecord
	 *            A DataRecord to decorate using the specified metadata
	 * 
	 * @throws DataDecaratorException
	 *             if the element does not exist
	 */
	void decorateRecord(String metadataGroupId, DataRecord dataRecord);
}
