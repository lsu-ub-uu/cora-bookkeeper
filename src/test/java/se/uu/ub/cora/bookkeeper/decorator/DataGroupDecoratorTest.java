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

package se.uu.ub.cora.bookkeeper.decorator;

import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.bookkeeper.recordpart.MetadataGroupSpy;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class DataGroupDecoratorTest {
	private DataGroupDecorator dataGroupDecorator;
	private DataGroupSpy dataGroup;
	private DataChildDecoratorFactorySpy dataChildDecoratorFactorySpy;
	private MetadataHolderSpy metadataHolder;
	private MetadataGroupSpy metadataGroupSpy;
	private MetadataMatchDataFactorySpy metadataMatchFactory;
	private List<DataChild> dataGroupGetChildrenList;
	private List<MetadataChildReference> childReferenceList;
	private MetadataMatchDataSpy matcher;

	@BeforeMethod
	public void beforMethod() {
		metadataHolder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolder);
		dataChildDecoratorFactorySpy = new DataChildDecoratorFactorySpy();
		setUpMetadataMatchFactory();
		createMetadataGroup();

		createDataGroup();

		dataGroupDecorator = new DataGroupDecorator(dataChildDecoratorFactorySpy,
				metadataMatchFactory, metadataGroupSpy);
	}

	private void setUpMetadataMatchFactory() {
		metadataMatchFactory = new MetadataMatchDataFactorySpy();
		createMatcher();
	}

	private void createMatcher() {
		matcher = new MetadataMatchDataSpy();
		metadataMatchFactory.MRV.setDefaultReturnValuesSupplier("factor", () -> matcher);
	}

	private void createMetadataGroup() {
		metadataGroupSpy = new MetadataGroupSpy("someId", "someNameInData", "someTextId",
				"someDefTextId");
		childReferenceList = new ArrayList<>();
		metadataGroupSpy.MRV.setDefaultReturnValuesSupplier("getChildReferences",
				() -> childReferenceList);
	}

	private void createDataGroup() {
		dataGroup = new DataGroupSpy();
		dataGroupGetChildrenList = new ArrayList<>();
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren", () -> dataGroupGetChildrenList);
	}

	@Test
	public void testDecorateOneChild() {
		testSetupOneDataChildOneMetadataChild();

		dataGroupDecorator.decorateData(dataGroup);

		assertNumberOfDecoratedChildren(1);
		assertNumberOfDataChildMatchingMetadataAttempts(1);
		assertDataChildrenAreDecorated();
	}

	@Test
	public void testDecorateThreeDataChildAndFourMetadataChild() {
		testSetupThreeDataChildrenFourMetadataChildren();

		dataGroupDecorator.decorateData(dataGroup);

		assertNumberOfDecoratedChildren(3);
		assertNumberOfDataChildMatchingMetadataAttempts(9);
		assertDataChildrenAreDecorated();
	}

	private void assertDataChildrenAreDecorated() {
		int i = 1;
		for (var dataChild : dataGroupGetChildrenList) {
			String metadataId = "text" + i + "Id";
			assertDataChildDecorated(metadataId, dataChild);
			i++;
		}
	}

	private void testSetupThreeDataChildrenFourMetadataChildren() {
		createMetadataChildReference("text4Id");
		testSetupOneDataChildOneMetadataChild();
		createMatchingDataChildAndMetadata(matcher, "text2Id", "text2NameInData");
		createMatchingDataChildAndMetadata(matcher, "text3Id", "text3NameInData");
	}

	private void assertNumberOfDataChildMatchingMetadataAttempts(int calledNumberOfTimes) {
		metadataMatchFactory.MCR.assertNumberOfCallsToMethod("factor", calledNumberOfTimes);
	}

	private void assertNumberOfDecoratedChildren(int calledNumberOfTimes) {
		dataChildDecoratorFactorySpy.MCR.assertNumberOfCallsToMethod("factor", calledNumberOfTimes);
	}

	private void testSetupOneDataChildOneMetadataChild() {
		createMatchingDataChildAndMetadata(matcher, "text1Id", "text1NameInData");
	}

	private void assertDataChildDecorated(String metadataId, DataChild dataChild) {
		var childDecorator = (DataChildDecoratorSpy) dataChildDecoratorFactorySpy.MCR
				.assertCalledParametersReturn("factor", metadataId);
		childDecorator.MCR.assertCalledParameters("decorateData", dataChild);
	}

	private void createMatchingDataChildAndMetadata(MetadataMatchDataSpy matcher, String metadataId,
			String nameInData) {
		createMetadataChildReference(metadataId);
		MetadataElementSpy childElement = setupMetadataHolderWithId(metadataId);
		DataAtomicSpy textVariableChild = createAtomicWithNameInData(nameInData);

		matcher.MRV.setSpecificReturnValuesSupplier("metadataSpecifiesData",
				this::createValidAnswer, childElement, textVariableChild);
	}

	private MetadataElementSpy setupMetadataHolderWithId(String metadataId) {
		MetadataElementSpy childElement = new MetadataElementSpy();
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement", () -> childElement,
				metadataId);
		return childElement;
	}

	private ValidationAnswerSpy createValidAnswer() {
		ValidationAnswerSpy validAnswer = new ValidationAnswerSpy();
		validAnswer.MRV.setDefaultReturnValuesSupplier("dataIsValid", () -> true);
		return validAnswer;
	}

	private DataAtomicSpy createAtomicWithNameInData(String nameInData) {
		DataAtomicSpy textVariableChild = new DataAtomicSpy();
		textVariableChild.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		dataGroupGetChildrenList.add(textVariableChild);
		return textVariableChild;
	}

	private void createMetadataChildReference(String metadataId) {
		MetadataChildReference childReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadata",
						metadataId, 1, 1);
		childReferenceList.add(childReference);
	}

	@Test
	public void testOnlyForTestGetMetadataElement() {
		assertSame(dataGroupDecorator.onlyForTestGetMetadataElement(), metadataGroupSpy);
	}

	@Test
	public void testOnlyForTestGetDataElementValidatorFactory() {
		assertSame(dataGroupDecorator.onlyForTestGetDataElementValidatorFactory(),
				dataChildDecoratorFactorySpy);
	}

	@Test
	public void testOnlyForTestGetMetadataMatchFactory() {
		assertSame(dataGroupDecorator.onlyForTestGetMetadataMatchFactory(), metadataMatchFactory);
	}
}
