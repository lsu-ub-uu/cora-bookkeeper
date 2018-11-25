package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;

public class DataGroupToNumberVariableConverterTest {
	private DataGroup dataGroup;
	private DataGroupToNumberVariableConverter converter;
	private NumberVariable numberVariable;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroup();
		converter = DataGroupToNumberVariableConverter.fromDataGroup(dataGroup);
		numberVariable = (NumberVariable) converter.toMetadata();
	}

	@Test
	public void testStandardParameters() {
		assertEquals(numberVariable.getId(), "someNumberVar");
		assertEquals(numberVariable.getNameInData(), "someNameInData");
		assertEquals(numberVariable.getTextId(), "someText");
		assertEquals(numberVariable.getDefTextId(), "someDefText");
	}

	@Test
	public void testnumberSpecificParameters() {
		assertEquals(numberVariable.getMin(), 0.0);
		assertEquals(numberVariable.getMax(), 5.0);
		assertEquals(numberVariable.getWarningMin(), 1.0);
		assertEquals(numberVariable.getWarningMax(), 4.0);
		assertEquals(numberVariable.getNumOfDecmials(), 1);
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = createRecordInfo();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
		dataGroup.addChild(createTextGroupUsingNameInDataAndTextId("textId", "someText"));
		dataGroup.addChild(createTextGroupUsingNameInDataAndTextId("defTextId", "someDefText"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("min", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("max", "5"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("warningMin", "1"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("warningMax", "4"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("warningMax", "4"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("numberOfDecimals", "1"));
		return dataGroup;
	}

	private DataGroup createRecordInfo() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "someNumberVar"));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private DataGroup createTextGroupUsingNameInDataAndTextId(String nameInData, String textId) {
		DataGroup textIdGroup = DataGroup.withNameInData(nameInData);
		textIdGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "coraText"));
		textIdGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", textId));
		return textIdGroup;
	}

}
