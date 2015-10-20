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

import java.util.ArrayList;
import java.util.List;

public class PresentationGroup implements PresentationElement {

	private String id;
	private String refGroupId;
	private List<PresentationChildReference> childReferences = new ArrayList<>();

	public PresentationGroup(String id, String refGroupId) {
		this.id = id;
		this.refGroupId = refGroupId;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getRefGroupId() {
		return refGroupId;
	}

	public void addChild(PresentationChildReference childReference) {
		childReferences.add(childReference);
	}

	public List<PresentationChildReference> getChildReferences() {
		return childReferences;
	}

}
