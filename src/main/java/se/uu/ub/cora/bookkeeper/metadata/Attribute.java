/*
 * Copyright 2022 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata;

import java.util.Collections;
import java.util.List;

/**
 * Attribute holds nameInData and possible values for a specific attribute as specified in metadata.
 * If the attribute has a final value SHOULD that be handled by just adding a list with the final
 * value to the attribute.
 */
public class Attribute {
	public static Attribute createAttributeUsingNameInDataAndValueList(String nameInData,
			List<String> values) {
		return new Attribute(nameInData, values);
	}

	public String nameInData = "";

	/**
	 * List with the values of an Attribute defined in Metadata.
	 */
	public List<String> values = Collections.emptyList();

	public Attribute() {
	}

	private Attribute(String nameInData, List<String> values) {
		this.nameInData = nameInData;
		this.values = values;
	}

}
