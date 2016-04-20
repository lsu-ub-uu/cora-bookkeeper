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

package se.uu.ub.cora.bookkeeper.data;

import java.util.LinkedHashSet;
import java.util.Set;

public final class DataRecord implements Data {
	Set<String> keys = new LinkedHashSet<>();
	private DataGroup dataGroup;

	public static DataRecord withDataGroup(DataGroup dataGroup) {
		return new DataRecord(dataGroup);
	}

	private DataRecord(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public void addKey(String key) {
		keys.add(key);
	}

	public boolean containsKey(String key) {
		return keys.contains(key);
	}

	public void setDataGroup(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public DataGroup getDataGroup() {
		return dataGroup;
	}

	public Set<String> getKeys() {
		return keys;
	}

}
