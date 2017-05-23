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

package se.uu.ub.cora.bookkeeper.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * MetadataChildReference is used to hold information about a child in metadata
 * groups.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public final class MetadataChildReference {

	public static final int UNLIMITED = Integer.MAX_VALUE;

	private String linkedRecordType;
	private String linkedRecordId;
	private final int repeatMin;
	private final int repeatMax;

	private boolean secret = false;

	private boolean readOnly = false;

	private String repeatMinKey = "";

	private String secretKey = "";

	private String readOnlyKey = "";

	private List<String> searchTerms = new ArrayList<>();

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

	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	public boolean isSecret() {
		return secret;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setRepeatMinKey(String repeatMinKey) {
		this.repeatMinKey = repeatMinKey;
	}

	public String getRepeatMinKey() {
		return repeatMinKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setReadOnlyKey(String readOnlyKey) {
		this.readOnlyKey = readOnlyKey;

	}

	public String getReadOnlyKey() {
		return readOnlyKey;
	}

	public String getLinkedRecordType() {
		return linkedRecordType;
	}

	public String getLinkedRecordId() {
		return linkedRecordId;
	}

	public List<String> getSearchTerms() {
		return searchTerms;
	}

	public void addSearchTerm(String searchTerm) {
		searchTerms.add(searchTerm);

	}
}
