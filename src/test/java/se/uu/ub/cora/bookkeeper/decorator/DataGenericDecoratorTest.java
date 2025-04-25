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

import java.util.LinkedHashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.bookkeeper.text.TextElementSpy;
import se.uu.ub.cora.bookkeeper.text.TextHolderProvider;
import se.uu.ub.cora.bookkeeper.text.Translation;
import se.uu.ub.cora.data.spies.DataAtomicSpy;

public class DataGenericDecoratorTest {
	private DataChildDecorator decorator;
	private DataAtomicSpy genericVariable;
	private TextHolderSpy textHolder;
	private MetadataElementSpy metadataElement;

	@BeforeMethod
	public void beforeMethod() {
		textHolder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(textHolder);

		metadataElement = new MetadataElementSpy();

		textHolder.MRV.setDefaultReturnValuesSupplier("getTextElement", this::createTextElement);

		createTextElement();

		decorator = new DataGenericDecorator(metadataElement);
		genericVariable = new DataAtomicSpy();
	}

	private TextElementSpy createTextElement() {
		TextElementSpy textElement = new TextElementSpy();
		Set<Translation> translations = new LinkedHashSet<>();
		translations.add(new Translation("en", "a text"));
		translations.add(new Translation("sv", "en text"));
		textElement.MRV.setDefaultReturnValuesSupplier("getTranslations", () -> translations);
		return textElement;
	}

	@Test
	public void testDecorate() {
		decorator.decorateData(genericVariable);

		textHolder.MCR.assertParameters("getTextElement", 0, "someTextId");
		genericVariable.MCR.assertCalledParameters("addAttributeByIdWithValue", "_sv", "en text");
		genericVariable.MCR.assertCalledParameters("addAttributeByIdWithValue", "_en", "a text");
	}

	@Test
	public void testRunExtraDecorator() {
		DataChildDecoratorSpy extraDecorator = new DataChildDecoratorSpy();
		((DataGenericDecorator) decorator).setExtraDecorator(extraDecorator);

		decorator.decorateData(genericVariable);

		extraDecorator.MCR.assertParameters("decorateData", 0, genericVariable);
	}

	@Test
	public void testonlyForTestGetExtraDecorator() {
		DataChildDecoratorSpy extraDecoratorIn = new DataChildDecoratorSpy();
		((DataGenericDecorator) decorator).setExtraDecorator(extraDecoratorIn);

		var extraDecoratorOut = ((DataGenericDecorator) decorator).onlyForTestGetExtraDecorator();

		assertSame(extraDecoratorOut, extraDecoratorIn);
	}
}
