/*
 * Copyright 2017 Uppsala University Library
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

/**
 * CollectTermLink holds information about a link to a collect term in metadata.
 */
public final class CollectTermLink {
	public final String type;
	public final String id;

	private CollectTermLink(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public static CollectTermLink createCollectTermWithTypeAndId(String type, String id) {
		return new CollectTermLink(type, id);
	}
}
