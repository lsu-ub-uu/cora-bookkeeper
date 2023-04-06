/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.recordtype.internal;

import java.util.function.Supplier;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public final class DataCreatorSpider {
	private static final String SELF_PRESENTATION_VIEW_ID = "selfPresentationViewId";
	private static final String USER_SUPPLIED_ID = "userSuppliedId";
	private static final String SEARCH_PRESENTATION_FORM_ID = "searchPresentationFormId";
	private static final String SEARCH_METADATA_ID = "searchMetadataId";
	private static final String LIST_PRESENTATION_VIEW_ID = "listPresentationViewId";
	private static final String NEW_PRESENTATION_FORM_ID = "newPresentationFormId";
	private static final String PRESENTATION_FORM_ID = "presentationFormId";
	private static final String PRESENTATION_VIEW_ID = "presentationViewId";
	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String RECORD_TYPE = "recordType";

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndPublicRead(
			String id, String userSuppliedId, String abstractValue, String publicRead) {
		DataGroup dataGroup = createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(
				id, userSuppliedId, abstractValue, null, publicRead);
		dataGroup.addChild(createLinkWithLinkedId("filter", "metadataGroup", "someFilterId"));
		return dataGroup;
	}

	private static DataAtomicSpy createDataAtomicSpyUsingNameAndValue(String name, String value) {
		DataAtomicSpy dataAtomicSpy = new DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		return dataAtomicSpy;
	}

	public static DataGroup createRecordTypeWithIdAndUserSuppliedId(String id,
			String userSuppliedId) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = new DataGroupSpiderOldSpy(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(RECORD_TYPE, id));

		dataGroup.addChild(createLinkWithLinkedId(METADATA_ID, "metadataGroup", id));

		dataGroup.addChild(createLinkWithLinkedId(PRESENTATION_VIEW_ID, "presentationGroup",
				"pg" + idWithCapitalFirst + "View"));

		dataGroup.addChild(createLinkWithLinkedId(PRESENTATION_FORM_ID, "presentationGroup",
				"pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(createLinkWithLinkedId(NEW_METADATA_ID, "metadataGroup", id + "New"));

		dataGroup.addChild(createLinkWithLinkedId(NEW_PRESENTATION_FORM_ID, "presentationGroup",
				"pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(createLinkWithLinkedId(LIST_PRESENTATION_VIEW_ID, "presentationGroup",
				"pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue(USER_SUPPLIED_ID, userSuppliedId));
		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue(SELF_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "Self"));
		return dataGroup;
	}

	private static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(
			String id, String userSuppliedId, String abstractValue, String parentId,
			String publicRead) {
		DataGroup dataGroup = createRecordTypeWithIdAndUserSuppliedId(id, userSuppliedId);
		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue("abstract", abstractValue));
		dataGroup.addChild(createDataAtomicSpyUsingNameAndValue("public", publicRead));
		if (null != parentId) {
			dataGroup.addChild(createLinkWithLinkedId("parentId", "recordType", parentId));
		}
		return dataGroup;
	}

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndParentId(String id,
			String userSuppliedId, String parentId) {
		return createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentIdAndPublicRead(id,
				userSuppliedId, "false", parentId, "false");
	}

	public static DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType,
			String recordId) {
		DataGroup recordInfo = new DataGroupSpiderOldSpy("recordInfo");
		recordInfo.addChild(createLinkWithLinkedId("parentId", "recordType", "binary"));
		recordInfo.addChild(createLinkWithLinkedId("type", "recordType", recordType));
		recordInfo.addChild(createDataAtomicSpyUsingNameAndValue("id", recordId));
		return recordInfo;
	}

	public static DataRecordLink createLinkWithLinkedId(String nameInData, String linkedRecordType,
			String id) {
		DataRecordLinkSpy linkSpy = new DataRecordLinkSpy();
		linkSpy.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);
		linkSpy.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				(Supplier<String>) () -> id);
		return linkSpy;
	}

	public static DataGroup createDataGroupWithNameInDataTypeAndId(String nameInData,
			String recordType, String recordId) {
		DataGroup dataGroup = new DataGroupSpiderOldSpy(nameInData);
		DataGroup recordInfo = DataCreatorSpider
				.createRecordInfoWithRecordTypeAndRecordId(recordType, recordId);
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
