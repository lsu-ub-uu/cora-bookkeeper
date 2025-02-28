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
package se.uu.ub.cora.bookkeeper.metadata;

public class MetadataHolderProvider {

	private MetadataHolderProvider() {
		// not called
		throw new UnsupportedOperationException();
	}

	private static MetadataHolder metadataHolder;
	private static MetadataHolderPopulator metadataHolderPopulator;

	public static MetadataHolder getHolder() {
		if (metadataHolder == null) {
			if (metadataHolderPopulator == null) {
				metadataHolderPopulator = new MetadataHolderPopulatorImp();
			}
			metadataHolder = metadataHolderPopulator
					.createAndPopulateMetadataHolderFromMetadataStorage();
		}
		return metadataHolder;
	}

	static void onlyForTestSetHolder(MetadataHolder metadataHolderIn) {
		metadataHolder = metadataHolderIn;
	}

	static void onlyForTestSetMetadataHolderPopulator(
			MetadataHolderPopulator metadataHolderPopulatorIn) {
		metadataHolderPopulator = metadataHolderPopulatorIn;

	}

	static MetadataHolderPopulator onlyForTestGetMetadataHolderPopulator() {
		return metadataHolderPopulator;
	}
}
