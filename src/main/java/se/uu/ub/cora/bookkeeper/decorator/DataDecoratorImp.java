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

import se.uu.ub.cora.data.DataGroup;

class DataDecoratorImp implements DataDecarator {

	private DataChildDecoratorFactory dataDecoratorFactory;

	DataDecoratorImp(DataChildDecoratorFactory decoratorFactory) {
		this.dataDecoratorFactory = decoratorFactory;
	}

	@Override
	public void decorateData(String metadataId, DataGroup dataGroup) {
		var dataChildDecorator = dataDecoratorFactory.factor(metadataId);
		dataChildDecorator.decorateData(dataGroup);

	}

	DataChildDecoratorFactory onlyForTestGetDataChildDecoratorFactory() {
		return dataDecoratorFactory;
	}
}
