/*
 * Copyright 2017, 2019 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataGroup;

public class CollectedTermTest {
	@Test
	public void testCollectedTerm() {
		DataGroup extraData = new DataGroupOldSpy("extraData");
		CollectedTerm collectedTerm = CollectedTerm
				.createCollectedTermWithTypeAndIdAndNameInDataAndExtraData("someType", "someId",
						"someNameInData", extraData);
		assertEquals(collectedTerm.type, "someType");
		assertEquals(collectedTerm.id, "someId");
		assertEquals(collectedTerm.nameInData, "someNameInData");
		assertEquals(collectedTerm.extraData, extraData);
	}

}
