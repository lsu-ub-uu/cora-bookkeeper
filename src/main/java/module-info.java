/**
 * The bookkeeper module provides functionality needed to validate and process data based, on
 * information from metadata, in a Cora based system.
 * <p>
 * The name bookkeeper is a reference to bookkeepers who keeps the finances in order. Every piece of
 * data in a Cora based system is governed by the systems metadata and keeping that in order and
 * managing the data records from it, is of utmost importance.
 */
module se.uu.ub.cora.bookkeeper {
	// dependency on storage should be removed when recordTypeHandler no longer needs it
	requires se.uu.ub.cora.storage;
	requires transitive se.uu.ub.cora.data;
	requires se.uu.ub.cora.initialize;

	exports se.uu.ub.cora.bookkeeper.decorator;
	exports se.uu.ub.cora.bookkeeper.linkcollector;
	exports se.uu.ub.cora.bookkeeper.metadata;
	exports se.uu.ub.cora.bookkeeper.text;
	exports se.uu.ub.cora.bookkeeper.recordpart;
	exports se.uu.ub.cora.bookkeeper.storage;
	exports se.uu.ub.cora.bookkeeper.termcollector;
	exports se.uu.ub.cora.bookkeeper.validator;
	exports se.uu.ub.cora.bookkeeper.recordtype;

	exports se.uu.ub.cora.bookkeeper.metadata.converter;

	// TODO: might be needed as an intermediate step, copied from spider
	// exports se.uu.ub.cora.bookkeeper.recordtype.internal
	// to se.uu.ub.cora.userstorage, se.uu.ub.cora.metadatastorage;

}