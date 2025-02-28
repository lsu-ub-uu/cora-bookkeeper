/*
 * Copyright 2017, 2022 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.storage;

import java.util.Collection;
import java.util.Optional;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collected.CollectTerms;

/**
 * MetadataStorageView is the gateway interface from the metadata system to the storage system. This
 * interface makes the storage details decoupled from the logic surrounding the metadata.
 * <p>
 * Instances of MetadataStorageView SHOULD be obtained through
 * {@link MetadataStorageProvider#getStorageView()} for each thread that needs access to
 * MetadataStorageView.
 */
public interface MetadataStorageView {

	/**
	 * getMetadataElements returns a Collection of {@link DataGroup} with all metadata elements that
	 * exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 * 
	 * @return
	 */
	Collection<DataGroup> getMetadataElements();

	/**
	 * getMetadataElement returns a {@link MetadataElement} with the metadata element for the
	 * specified elementId record that exists in storage.
	 * </p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested element can not be read.
	 * </p>
	 * 
	 * @param elementId
	 *            A String with the id of the metadata element to get from storage
	 * @return A metadata element from storage
	 */
	MetadataElement getMetadataElement(String elementId);

	/**
	 * getPresentationElements returns a Collection of {@link DataGroup} with all presentation
	 * elements that exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 * 
	 * @return
	 */
	Collection<DataGroup> getPresentationElements();

	/**
	 * getTexts returns a Collection of {@link DataGroup} with all text elements that exists in
	 * storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 * 
	 * @return
	 */
	Collection<DataGroup> getTexts();

	/**
	 * getRecordTypes returns a Collection of {@link DataGroup} with all recordType elements that
	 * exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 * 
	 * @return
	 */
	Collection<DataGroup> getRecordTypes();

	/**
	 * getValidationType returns a {@link ValidationType} with the valiationType for the specified
	 * validationId record that exists in storage.
	 * </p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested element can not be read.
	 * </p>
	 * 
	 * @param validationId
	 *            A String with the id of the validation type to get from storage
	 * @return An Optional with the requested validationType or empty if it does not exsist in
	 *         storage
	 */
	Optional<ValidationType> getValidationType(String validationId);

	/**
	 * getValidationTypes returns a Collection of {@link ValidationType} with all valiationType
	 * elements that exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 */
	Collection<ValidationType> getValidationTypes();

	/**
	 * getCollectTermsAsDataGroup returns a Collection of {@link DataGroup} with all collectTerm
	 * elements that exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty collection be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 * 
	 * @return
	 */
	Collection<DataGroup> getCollectTermsAsDataGroup();

	/**
	 * getCollectTermHolder returns a {@link CollectTermHolder} with all {@link CollectTerm}
	 * definitions that exists in storage.
	 * </p>
	 * If no elements exist SHOULD an empty {@link CollectTermHolder} be returned.
	 * <p>
	 * If a problem occurs while reading from storage MUST a {@link MetadataStorageViewException} be
	 * thrown, indicating that the requested elements can not be read.
	 * </p>
	 *
	 * @return A {@link CollectTermHolder} populated with {@link CollectTerms}
	 */
	CollectTermHolder getCollectTermHolder();

}
