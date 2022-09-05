/*
 * Copyright 2019 Uppsala University Library
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataAtomicFactorySpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataGroupFactorySpy;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class CollectedDataCreatorTest {

	private CollectedDataCreator dataCreator;
	private Map<String, List<DataGroup>> collectedTerms;
	private List<DataGroup> indexDataGroups;
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataCreator = new CollectedDataCreatorImp();
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		collectedTerms = new HashMap<>();
		indexDataGroups = new ArrayList<>();
	}

	@Test
	public void testCreateCollectedDataNoCollectTerms() {
		DataGroup dataGroup = createBasicDataGroup();
		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(new HashMap<>(), dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 2);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");
	}

	@Test
	public void testCreateCollectedDataOneCollectIndexTerm() {
		DataGroup dataGroup = createBasicDataGroup();

		DataGroup dataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(dataTerm);
		collectedTerms.put("index", indexDataGroups);

		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(collectedTerms, dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 3);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");

		DataGroup indexDataGroup = result.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
	}

	@Test
	public void testCreateCollectedDataTwoCollectTerms() {
		DataGroup dataGroup = createBasicDataGroup();

		DataGroup dataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(dataTerm);
		DataGroup dataTerm2 = createCollectedDataTerm("Some title", "titleSecondIndexTerm");
		indexDataGroups.add(dataTerm2);
		collectedTerms.put("index", indexDataGroups);

		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(collectedTerms, dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 3);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");

		DataGroup indexDataGroup = result.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 1, "index");

	}

	private void assertCollectedTermInReturnedGroupIsSameAsSentIn(DataGroup indexDataGroup,
			int index, String key) {
		DataGroup collectedDataTerm = indexDataGroup.getAllGroupsWithNameInData("collectedDataTerm")
				.get(index);
		assertEquals(collectedDataTerm, collectedTerms.get(key).get(index));
		assertEquals(collectedDataTerm.getRepeatId(), String.valueOf(index));
	}

	@Test
	public void testCreateCollectedDataTwoDifferentTypesOfCollectTerms() {
		DataGroup dataGroup = createBasicDataGroup();

		DataGroup indexDataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(indexDataTerm);
		DataGroup indexDataTerm2 = createCollectedDataTerm("Some sub title", "subTitleIndexTerm");
		indexDataGroups.add(indexDataTerm2);
		collectedTerms.put("index", indexDataGroups);

		List<DataGroup> storageDataGroups = new ArrayList<>();
		DataGroup storageDataTerm = createCollectedDataTerm("Some name", "nameIndexTerm");
		storageDataGroups.add(storageDataTerm);
		collectedTerms.put("storage", storageDataGroups);

		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(collectedTerms, dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 4);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");

		DataGroup indexDataGroup = result.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 1, "index");

		DataGroup storageDataGroup = result.getFirstGroupWithNameInData("storage");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(storageDataGroup, 0, "storage");

	}

	private DataGroup createBasicDataGroup() {
		DataGroup book = new DataGroupOldSpy("book");
		DataGroup recordInfo = createRecordInfo("book", "book:111", "testSystem");
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfo(String type, String id, String dataDivider) {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", id));
		DataGroup typeGroup = new DataGroupOldSpy("type");
		typeGroup.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		typeGroup.addChild(new DataAtomicSpy("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
		DataGroup dataDividerGroup = new DataGroupOldSpy("dataDivider");
		dataDividerGroup.addChild(new DataAtomicSpy("linkedRecordType", "system"));
		dataDividerGroup.addChild(new DataAtomicSpy("linkedRecordId", dataDivider));
		recordInfo.addChild(dataDividerGroup);
		return recordInfo;
	}

	private DataGroup createCollectedDataTerm(String collectTermValue, String collectTermId) {
		DataGroup dataTerm = new DataGroupOldSpy("collectedDataTerm");
		dataTerm.addChild(new DataAtomicSpy("collectTermId", collectTermId));
		dataTerm.addChild(new DataAtomicSpy("collectTermValue", collectTermValue));
		DataGroup extraData = new DataGroupOldSpy("extraData");
		extraData.addChild(new DataAtomicSpy("indexType", "indexTypeString"));
		dataTerm.addChild(extraData);
		dataTerm.addAttributeByIdWithValue("type", "index");
		return dataTerm;
	}

	/**
	 * WTAI Without Type and Id
	 */

	@Test
	public void testWATICreateCollectedDataNoCollectTerms() {
		HashMap<String, List<DataGroup>> collectedTerms = new HashMap<>();

		DataGroup returnedCollectedData = dataCreator
				.createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(collectedTerms);

		assertEquals(returnedCollectedData.getNameInData(), "collectedData");
		assertEquals(returnedCollectedData.getChildren().size(), 0);
	}

	@Test
	public void testWTAICreateCollectedDataOneCollectIndexTerm() {
		DataGroup dataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(dataTerm);
		collectedTerms.put("index", indexDataGroups);

		DataGroup returnedCollectedData = dataCreator
				.createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(collectedTerms);

		assertEquals(returnedCollectedData.getNameInData(), "collectedData");
		assertEquals(returnedCollectedData.getChildren().size(), 1);

		DataGroup indexDataGroup = returnedCollectedData.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
	}

	@Test
	public void testWTAICreateCollectedDataTwoCollectTerms() {

		DataGroup dataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(dataTerm);
		DataGroup dataTerm2 = createCollectedDataTerm("Some title", "titleSecondIndexTerm");
		indexDataGroups.add(dataTerm2);
		collectedTerms.put("index", indexDataGroups);

		DataGroup returnedCollectedData = dataCreator
				.createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(collectedTerms);

		assertEquals(returnedCollectedData.getNameInData(), "collectedData");
		assertEquals(returnedCollectedData.getChildren().size(), 1);

		DataGroup indexDataGroup = returnedCollectedData.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 1, "index");

	}

	@Test
	public void testWTAICreateCollectedDataTwoDifferentTypesOfCollectTerms() {

		DataGroup indexDataTerm = createCollectedDataTerm("Some title", "titleIndexTerm");
		indexDataGroups.add(indexDataTerm);
		DataGroup indexDataTerm2 = createCollectedDataTerm("Some sub title", "subTitleIndexTerm");
		indexDataGroups.add(indexDataTerm2);
		collectedTerms.put("index", indexDataGroups);

		List<DataGroup> storageDataGroups = new ArrayList<>();
		DataGroup storageDataTerm = createCollectedDataTerm("Some name", "nameIndexTerm");
		storageDataGroups.add(storageDataTerm);
		collectedTerms.put("storage", storageDataGroups);

		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(collectedTerms);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 2);

		DataGroup indexDataGroup = result.getFirstGroupWithNameInData("index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 0, "index");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(indexDataGroup, 1, "index");

		DataGroup storageDataGroup = result.getFirstGroupWithNameInData("storage");
		assertCollectedTermInReturnedGroupIsSameAsSentIn(storageDataGroup, 0, "storage");

	}
}
