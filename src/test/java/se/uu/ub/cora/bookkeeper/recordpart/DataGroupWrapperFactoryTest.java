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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperFactoryTest {

	@Test
	public void testFactor() {
		DataGroupWrapperFactoryImp factory = new DataGroupWrapperFactoryImp();
		DataGroup dataGroup = new DataGroupSpy("someNameInData");
		DataGroupWrapper wrapper = factory.factor(dataGroup);
		assertSame(wrapper.getDataGroup(), dataGroup);
	}
}
