/*
 * Copyright 2015, 2020 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

/**
 * MetadataChildReference is used to hold information about a child in metadata groups.
 * 
 */
public final class MetadataChildReference {

	public static final int UNLIMITED = Integer.MAX_VALUE;

	private String linkedRecordType;
	private String linkedRecordId;
	private final int repeatMin;
	private final int repeatMax;

	private List<CollectTermLink> collectTerms = new ArrayList<>();

	private ConstraintType recordPartConstraint;

	public MetadataChildReference(String linkedRecordType, String linkedRecordId, int repeatMin,
			int repeatMax) {
		this.linkedRecordType = linkedRecordType;
		this.linkedRecordId = linkedRecordId;
		this.repeatMin = repeatMin;
		this.repeatMax = repeatMax;
	}

	public static MetadataChildReference withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax(
			String linkedRecordType, String linkedRecordId, int repeatMin, int repeatMax) {
		return new MetadataChildReference(linkedRecordType, linkedRecordId, repeatMin, repeatMax);
	}

	public int getRepeatMin() {
		return repeatMin;
	}

	public int getRepeatMax() {
		return repeatMax;
	}

	public String getLinkedRecordType() {
		return linkedRecordType;
	}

	public String getLinkedRecordId() {
		return linkedRecordId;
	}

	public List<CollectTermLink> getCollectTerms() {
		return collectTerms;
	}

	public void addCollectTerm(CollectTermLink collectTerm) {
		collectTerms.add(collectTerm);

	}

	public void setRecordPartConstraint(ConstraintType recordPartConstraint) {
		this.recordPartConstraint = recordPartConstraint;

	}

	public ConstraintType getRecordPartConstraint() {
		return recordPartConstraint;
	}
}
