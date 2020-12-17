/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.data.DataGroup;

public class MatcherFactoryImp implements MatcherFactory {

	private MetadataMatchData dataMatcher;

	public MatcherFactoryImp(MetadataMatchData dataMatcher) {
		this.dataMatcher = dataMatcher;
	}

	@Override
	public Matcher factor(DataGroup dataGroup, MetadataGroup metadataGroup) {
		return new GroupMatcher(dataMatcher, dataGroup, metadataGroup);
	}

}
