/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.termcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.PermissionTerm.Mode;
import se.uu.ub.cora.bookkeeper.recordtype.internal.CollectTermHolderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.data.collected.PermissionTerm;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class PermissionTermDataHandlerTest {

	private MetadataStorageViewSpy metadataStorage;
	private LoggerFactorySpy loggerFactory;
	private CollectTermHolderSpy collectTermHolder;
	private PermissionTermDataHandler handler;
	private List<PermissionTerm> currentPermissionTerms;
	private List<PermissionTerm> previousPermissionTerms;

	@BeforeMethod
	public void setUp() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setUpMetadataStorageForTest();

		previousPermissionTerms = createPreviousPermissionTerms();
		currentPermissionTerms = createCurrentPermissionTerms();

		handler = new PermissionTermDataHandlerImp();
	}

	private void setUpMetadataStorageForTest() {
		metadataStorage = new MetadataStorageViewSpy();
		collectTermHolder = new CollectTermHolderSpy();
		metadataStorage.MRV.setDefaultReturnValuesSupplier("getCollectTermHolder",
				() -> collectTermHolder);

		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorage);
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);
	}

	private List<PermissionTerm> createPreviousPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_1", "previous_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_2", "previous_2", "someKey"));
		permissionTerms.add(new PermissionTerm("id_3", "previous_3", "someKey"));

		return permissionTerms;
	}

	private List<PermissionTerm> createCurrentPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_1", "current_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_2", "current_2", "someKey"));
		permissionTerms.add(new PermissionTerm("id_3", "current_3", "someKey"));
		return permissionTerms;
	}

	@Test
	public void testGetEmptyList() {
		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(Collections.emptyList(),
						Collections.emptyList());

		assertTrue(statePermissionsValues.isEmpty());
	}

	@Test
	public void testAllPermissionAreStandard() {
		createPermissionTermMetadata(MetaPermTerm.standard("id_1"));
		createPermissionTermMetadata(MetaPermTerm.standard("id_2"));
		createPermissionTermMetadata(MetaPermTerm.standard("id_3"));

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues,
				new PermissionTermPair("id_1", "current_1"),
				new PermissionTermPair("id_2", "current_2"),
				new PermissionTermPair("id_3", "current_3"));
	}

	@Test
	public void testAllPermissionAreState() {
		createPermissionTermMetadata(MetaPermTerm.state("id_1"));
		createPermissionTermMetadata(MetaPermTerm.state("id_2"));
		createPermissionTermMetadata(MetaPermTerm.state("id_3"));

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues,
				new PermissionTermPair("id_1", "previous_1"),
				new PermissionTermPair("id_2", "previous_2"),
				new PermissionTermPair("id_3", "previous_3"));
	}

	@Test
	public void testAllPermissionAreMixed() {
		createPermissionTermMetadata(MetaPermTerm.standard("id_1"));
		createPermissionTermMetadata(MetaPermTerm.state("id_2"));
		createPermissionTermMetadata(MetaPermTerm.standard("id_3"));

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues,
				new PermissionTermPair("id_1", "current_1"),
				new PermissionTermPair("id_2", "previous_2"),
				new PermissionTermPair("id_3", "current_3"));
	}

	@Test
	public void test_PermssionTermWithSameId() {
		createPermissionTermMetadata(MetaPermTerm.state("id_4"));

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(
						createPreviousSameIdPermissionTerms(),
						createCurrentSameIdPermissionTerms());

		assertCorrectTermAndValueExists(statePermissionsValues,
				new PermissionTermPair("id_4", "previous_4_1"),
				new PermissionTermPair("id_4", "previous_4_2"),
				new PermissionTermPair("id_4", "previous_4_3"));
	}

	private List<PermissionTerm> createPreviousSameIdPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_4", "previous_4_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_4", "previous_4_2", "someKey"));
		permissionTerms.add(new PermissionTerm("id_4", "previous_4_3", "someKey"));
		return permissionTerms;
	}

	private List<PermissionTerm> createCurrentSameIdPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_4", "current_4_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_4", "current_4_2", "someKey"));
		return permissionTerms;
	}

	@Test
	public void test_PermssionTermOnlyExistsInCurrent() {
		createPermissionTermMetadata(MetaPermTerm.state("id_5"));

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(Collections.emptyList(),
						createCurrentOnlyExistsInCurrent());

		assertEquals(statePermissionsValues.size(), 0);
	}

	private List<PermissionTerm> createCurrentOnlyExistsInCurrent() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_5", "current_5", "someKey"));
		return permissionTerms;
	}

	private se.uu.ub.cora.bookkeeper.metadata.PermissionTerm createMetadataPermissionTerm(String id,
			Mode state) {
		return se.uu.ub.cora.bookkeeper.metadata.PermissionTerm
				.usingIdAndNameInDataAndPermissionKeyAndMode(id, "someNameInData", "someKey",
						state);
	}

	private void assertCorrectTermAndValueExists(List<PermissionTerm> statePermissionsValues,
			PermissionTermPair... expectedIdAndValues) {
		var permissionTermIdAndValue = convertToListUsingIdAndValueToMap(statePermissionsValues);

		assertEquals(permissionTermIdAndValue.size(), 3);
		assertExpectedPermissionTermsAndValuesExists(permissionTermIdAndValue, expectedIdAndValues);
	}

	private void assertExpectedPermissionTermsAndValuesExists(List<String> permissionTermIdAndValue,
			PermissionTermPair... expectedIdAndValues) {
		for (PermissionTermPair pair : expectedIdAndValues) {
			assertTrue(permissionTermIdAndValue.contains(pair.id() + pair.value));
		}
	}

	private List<String> convertToListUsingIdAndValueToMap(List<PermissionTerm> list) {
		return list.stream().map(perm -> perm.id() + perm.value()).toList();
	}

	record PermissionTermPair(String id, String value) {
	}

	record MetaPermTerm(String id, Mode mode) {
		public static MetaPermTerm standard(String id) {
			return new MetaPermTerm(id, Mode.STANDARD);
		}

		public static MetaPermTerm state(String id) {
			return new MetaPermTerm(id, Mode.STATE);
		}
	}

	private void createPermissionTermMetadata(MetaPermTerm... metaPermissionTerms) {

		for (MetaPermTerm metaPermissionTerm : metaPermissionTerms) {
			var metadataPermissionTerm = createMetadataPermissionTerm(metaPermissionTerm.id(),
					metaPermissionTerm.mode());
			collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
					() -> metadataPermissionTerm, metaPermissionTerm.id());
		}

	}

}
