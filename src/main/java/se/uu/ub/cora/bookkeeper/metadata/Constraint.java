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
package se.uu.ub.cora.bookkeeper.metadata;

import java.util.HashSet;
import java.util.Set;

import se.uu.ub.cora.data.DataAttribute;

public class Constraint {

	private String nameInData;
	private Set<DataAttribute> dataAttributes = new HashSet<>();

	public Constraint(String nameInData) {
		this.nameInData = nameInData;
	}

	public String getNameInData() {
		return nameInData;
	}

	public void addAttribute(DataAttribute dataAttribute) {
		dataAttributes.add(dataAttribute);
	}

	public Set<DataAttribute> getDataAttributes() {
		return dataAttributes;
	}

	// @Override
	// public boolean equals(Object object) {
	// if (object == this) {
	// return true;
	// }
	// if (!(object instanceof Constraint)) {
	// return false;
	// }
	// Constraint constraint = (Constraint) object;
	// return compareConstraints(constraint);
	// }
	//
	// private boolean compareConstraints(Constraint constraint) {
	// if (differentNameInData(constraint) || differentNumberOfAttributes(constraint)) {
	// return false;
	// }
	//
	// for (DataAttribute dataAttribute : constraint.getDataAttributes()) {
	// if (!attributesContainsDataAttribute(dataAttribute)) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// private boolean differentNameInData(Constraint constraint) {
	// return !constraint.getNameInData().equals(nameInData);
	// }
	//
	// private boolean differentNumberOfAttributes(Constraint constraint) {
	// return constraint.getDataAttributes().size() != dataAttributes.size();
	// }
	//
	// private boolean attributesContainsDataAttribute(DataAttribute dataAttributeToCompare) {
	// for (DataAttribute dataAttribute : dataAttributes) {
	// if (sameAttributeNameInData(dataAttribute, dataAttributeToCompare)
	// && sameAttributeValue(dataAttribute, dataAttributeToCompare)) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private boolean sameAttributeNameInData(DataAttribute dataAttribute,
	// DataAttribute dataAttributeToCompare) {
	// return dataAttribute.getNameInData().equals(dataAttributeToCompare.getNameInData());
	// }
	//
	// private boolean sameAttributeValue(DataAttribute dataAttribute,
	// DataAttribute dataAttributeToCompare) {
	// return dataAttribute.getValue().equals(dataAttributeToCompare.getValue());
	// }
	//
	// @Override
	// public int hashCode() {
	// return Objects.hash(nameInData, dataAttributes);
	// }

}
