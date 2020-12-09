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

import static org.testng.Assert.assertSame;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {
	private DataRedactor dataRedactor;
	private DataGroupForDataRedactorSpy dataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private Set<String> titlePermissions;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;

	@BeforeMethod
	public void setUp() {
		MetadataHolder metadataHolder = new MetadataHolderSpy();
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy);
		dataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
	}

	private Set<Constraint> createReadConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	private Set<String> createReadPermissionForTitle() {
		Set<String> recordPartPermissions = new HashSet<>();
		recordPartPermissions.add("title");
		return recordPartPermissions;
	}

	@Test
	public void testRemoveWhenNoConstraints() throws Exception {
		String metadataId = "someMetadataId";
		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, dataGroupSpy, emptyConstraints, emptyPermissions);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		assertSame(filteredDataGroup, dataGroupSpy);
	}

	@Test
	public void testRemoveSomeConstraint() throws Exception {
		String metadataId = "someMetadataId";

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, dataGroupSpy, titleConstraints, emptyPermissions);

		dataGroupRedactorSpy.MCR.assertReturn("removeChildrenForConstraintsWithoutPermissions", 0,
				filteredDataGroup);
		dataGroupRedactorSpy.MCR.assertParameters("removeChildrenForConstraintsWithoutPermissions",
				0, dataGroupSpy, titleConstraints, emptyPermissions);
	}

	@Test
	public void testReplaceWhenNoConstraints() throws Exception {
		String metadataId = "";
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceWhenSomeConstraint() throws Exception {
		String metadataId = "";
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
				titlePermissions);

		dataGroupRedactorSpy.MCR.assertReturn("replaceChildrenForConstraintsWithoutPermissions", 0,
				replacedDataGroup);

		dataGroupRedactorSpy.MCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions",
				0, originalDataGroup, updatedDataGroup, titleConstraints, titlePermissions);
	}

}
