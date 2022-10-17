/**
 * The bookkeeper module provides functionality needed to validate and process data based, on
 * information from metadata, in a Cora based system.
 * <p>
 * The name bookkeeper is a reference to bookkeepers who keeps the finances in order. Every piece of
 * data in a Cora based system is governed by the systems metadata and keeping that in order and
 * managing the data records from it, is of utmost importance.
 */
module se.uu.ub.cora.bookkeeper {
	requires transitive se.uu.ub.cora.data;
	requires transitive se.uu.ub.cora.storage;
	requires se.uu.ub.cora.initialize;

	exports se.uu.ub.cora.bookkeeper.linkcollector;
	exports se.uu.ub.cora.bookkeeper.metadata;
	exports se.uu.ub.cora.bookkeeper.termcollector;
	exports se.uu.ub.cora.bookkeeper.validator;
	exports se.uu.ub.cora.bookkeeper.recordpart;
	exports se.uu.ub.cora.bookkeeper.storage;
}