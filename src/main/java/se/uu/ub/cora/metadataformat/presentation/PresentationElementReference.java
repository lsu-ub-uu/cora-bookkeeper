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

package se.uu.ub.cora.metadataformat.presentation;

public class PresentationElementReference implements PresentationChildReference {

	private String elementRef;
	private String presentationOf;
	private String elementRefMinimized;

	public PresentationElementReference(String elementRef, String presentationOf) {
		this.elementRef = elementRef;
		this.presentationOf = presentationOf;
	}

	public String getElementRef() {
		return elementRef;
	}

	public String getPresentationOf() {
		return presentationOf;
	}

	public void setElementRefMinimized(String elementRefMinimized) {
		this.elementRefMinimized = elementRefMinimized;
	}

	public String getElementRefMinimized() {
		return elementRefMinimized;
	}

	@Override
	public String getReferenceId() {
		return getElementRef();
	}

}
