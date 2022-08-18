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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.testspies.data.DataFactorySpy;
import se.uu.ub.cora.testspies.data.DataGroupSpy;

public class DataGroupRedactorTest {
	private DataGroupRedactor dataGroupRedactor;
	private DataGroupSpy dataGroupSpy;
	private DataGroupSpy originalDataGroup;
	private DataGroupSpy updatedDataGroup;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private Set<String> titlePermissions;
	private DataFactorySpy dataFactorySpy;
	private DataChildFilter titleConstraintChildFilter;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		dataGroupRedactor = new DataGroupRedactorImp();
		dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> true);
		originalDataGroup = new DataGroupSpy();
		originalDataGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> true);
		updatedDataGroup = new DataGroupSpy();
		updatedDataGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> true);
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
	}

	private Set<Constraint> createReadConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new LinkedHashSet<>();
		Constraint constraint = new Constraint("title");
		titleConstraintChildFilter = (DataChildFilter) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 0);
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	private Set<String> createReadPermissionForTitle() {
		Set<String> recordPartPermissions = new HashSet<>();
		recordPartPermissions.add("title");
		return recordPartPermissions;
	}

	@Test
	public void testRemoveNoConstraintsNoPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, emptyConstraints,
						emptyPermissions);

		assertSame(filteredDataGroup, dataGroupSpy);

		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");
	}

	@Test
	public void testRemoveNoConstraintsButPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, emptyConstraints,
						titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");
	}

	@Test
	public void testRemoveConstraintsAndPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, titleConstraints,
						titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");
	}

	@Test
	public void testRemoveNoChildToRemove() throws Exception {
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> false);

		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		dataGroupSpy.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
	}

	@Test
	public void testRemoveConstraintsNoPermissions() throws Exception {
		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		dataGroupSpy.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
	}

	@Test
	public void testRemoveMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));
		DataChildFilter otherConstraintChildFilter = (DataChildFilter) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 1);

		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		dataGroupSpy.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		dataGroupSpy.MCR.assertParameters("removeAllChildrenMatchingFilter", 1,
				otherConstraintChildFilter);
	}

	@Test
	public void testReplaceNoContrainsNoPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, emptyConstraints, emptyPermissions);

		assertSame(replacedDataGroup, updatedDataGroup);

		originalDataGroup.MCR.assertMethodNotCalled("getAllChildrenMatchingFilter");
		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");

	}

	@Test
	public void testReplaceNoConstraintsButPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, emptyConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		originalDataGroup.MCR.assertMethodNotCalled("getAllChildrenMatchingFilter");
		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");
	}

	@Test
	public void testReplaceConstraintsWithMatchingPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, titleConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		originalDataGroup.MCR.assertMethodNotCalled("getAllChildrenMatchingFilter");
		dataGroupSpy.MCR.assertMethodNotCalled("removeAllChildrenMatchingFilter");
	}

	@Test
	public void testReplaceMultipleConstraintsMatchingMultiplePermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));
		DataChildFilter otherConstraintChildFilter = (DataChildFilter) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 1);

		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, titlePermissions);

		updatedDataGroup.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				otherConstraintChildFilter);
		originalDataGroup.MCR.assertParameters("getAllChildrenMatchingFilter", 0,
				otherConstraintChildFilter);
		updatedDataGroup.MCR.assertParameters("addChildren", 0,
				originalDataGroup.MCR.getReturnValue("getAllChildrenMatchingFilter", 0));
	}

	@Test
	public void testReplaceConstraintsEmptyPermissions() throws Exception {
		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		updatedDataGroup.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		originalDataGroup.MCR.assertParameters("getAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		updatedDataGroup.MCR.assertParameters("addChildren", 0,
				originalDataGroup.MCR.getReturnValue("getAllChildrenMatchingFilter", 0));

	}

	@Test
	public void testReplaceMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));
		DataChildFilter otherConstraintChildFilter = (DataChildFilter) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 1);

		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		updatedDataGroup.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		originalDataGroup.MCR.assertParameters("getAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		updatedDataGroup.MCR.assertParameters("addChildren", 0,
				originalDataGroup.MCR.getReturnValue("getAllChildrenMatchingFilter", 0));

		updatedDataGroup.MCR.assertParameters("removeAllChildrenMatchingFilter", 1,
				otherConstraintChildFilter);
		originalDataGroup.MCR.assertParameters("getAllChildrenMatchingFilter", 1,
				otherConstraintChildFilter);
		updatedDataGroup.MCR.assertParameters("addChildren", 1,
				originalDataGroup.MCR.getReturnValue("getAllChildrenMatchingFilter", 1));

	}

	@Test
	public void testReplaceNoChildToRemove() throws Exception {
		updatedDataGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				(Supplier<Boolean>) () -> false);
		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		updatedDataGroup.MCR.assertParameters("removeAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		originalDataGroup.MCR.assertParameters("getAllChildrenMatchingFilter", 0,
				titleConstraintChildFilter);
		updatedDataGroup.MCR.assertParameters("addChildren", 0,
				originalDataGroup.MCR.getReturnValue("getAllChildrenMatchingFilter", 0));

	}
}
