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
import java.util.Map;
import java.util.stream.Collectors;

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

		currentPermissionTerms = createCurrentPermissionTerms();
		previousPermissionTerms = createPreviousPermissionTerms();

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

	private List<PermissionTerm> createCurrentPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_1", "current_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_2", "current_2", "someKey"));
		permissionTerms.add(new PermissionTerm("id_3", "current_3", "someKey"));
		return permissionTerms;
	}

	private List<PermissionTerm> createPreviousPermissionTerms() {
		List<PermissionTerm> permissionTerms = new ArrayList<>();
		permissionTerms.add(new PermissionTerm("id_1", "previous_1", "someKey"));
		permissionTerms.add(new PermissionTerm("id_2", "previous_2", "someKey"));
		permissionTerms.add(new PermissionTerm("id_3", "previous_3", "someKey"));
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
		var metaPermission1 = createMetadataPermissionTerm("id_1", Mode.STANDARD);
		var metaPermission2 = createMetadataPermissionTerm("id_2", Mode.STANDARD);
		var metaPermission3 = createMetadataPermissionTerm("id_3", Mode.STANDARD);
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission1, "id_1");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission2, "id_2");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission3, "id_3");

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues, new Pair("id_1", "current_1"),
				new Pair("id_2", "current_2"), new Pair("id_3", "current_3"));
	}

	@Test
	public void testAllPermissionAreState() {
		var metaPermission1 = createMetadataPermissionTerm("id_1", Mode.STATE);
		var metaPermission2 = createMetadataPermissionTerm("id_2", Mode.STATE);
		var metaPermission3 = createMetadataPermissionTerm("id_3", Mode.STATE);
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission1, "id_1");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission2, "id_2");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission3, "id_3");

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues, new Pair("id_1", "previous_1"),
				new Pair("id_2", "previous_2"), new Pair("id_3", "previous_3"));
	}

	@Test
	public void testAllPermissionAreMixed() {
		var metaPermission1 = createMetadataPermissionTerm("id_1", Mode.STANDARD);
		var metaPermission2 = createMetadataPermissionTerm("id_2", Mode.STATE);
		var metaPermission3 = createMetadataPermissionTerm("id_3", Mode.STANDARD);
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission1, "id_1");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission2, "id_2");
		collectTermHolder.MRV.setSpecificReturnValuesSupplier("getCollectTermById",
				() -> metaPermission3, "id_3");

		List<PermissionTerm> statePermissionsValues = handler
				.getMixedPermissionTermValuesConsideringModeState(previousPermissionTerms,
						currentPermissionTerms);

		assertCorrectTermAndValueExists(statePermissionsValues, new Pair("id_1", "current_1"),
				new Pair("id_2", "previous_2"), new Pair("id_3", "current_3"));
	}

	private se.uu.ub.cora.bookkeeper.metadata.PermissionTerm createMetadataPermissionTerm(String id,
			Mode state) {
		return se.uu.ub.cora.bookkeeper.metadata.PermissionTerm
				.usingIdAndNameInDataAndPermissionKeyAndMode(id, "someNameInData", "someKey",
						state);
	}

	private void assertCorrectTermAndValueExists(List<PermissionTerm> statePermissionsValues,
			Pair... expectedIdAndValues) {
		var permissionTermMap = convertListToMap(statePermissionsValues);

		assertEquals(permissionTermMap.size(), 3);
		assertExpectedPermissionTermsAndValuesExists(permissionTermMap, expectedIdAndValues);
	}

	private void assertExpectedPermissionTermsAndValuesExists(Map<String, String> permissionTermMap,
			Pair... expectedIdAndValues) {
		for (Pair pair : expectedIdAndValues) {
			assertTrue(permissionTermMap.containsKey(pair.id()));
			assertEquals(permissionTermMap.get(pair.id()), pair.value());
		}
	}

	private Map<String, String> convertListToMap(List<PermissionTerm> list) {
		return list.stream().collect(Collectors.toMap(PermissionTerm::id, PermissionTerm::value));
	}

	record Pair(String id, String value) {
	}
}
