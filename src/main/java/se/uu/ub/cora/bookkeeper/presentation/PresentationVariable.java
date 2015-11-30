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

package se.uu.ub.cora.bookkeeper.presentation;

public class PresentationVariable implements PresentationElement {

	private String id;
	private String refVarId;
	private Mode mode;

	public PresentationVariable(String id, String refVarId, Mode input) {
		this.id = id;
		this.refVarId = refVarId;
		this.mode = input;
	}

	public String getId() {
		return id;
	}

	public String getRefVarId() {
		return refVarId;
	}

	public Mode getMode() {
		return mode;
	}

	public enum Mode {
		INPUT("input"), OUTPUT("output");
		private String value;

		Mode(final String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
