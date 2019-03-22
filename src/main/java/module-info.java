module se.uu.ub.cora.bookkeeper {
	requires transitive se.uu.ub.cora.json;

	exports se.uu.ub.cora.bookkeeper.data;
	exports se.uu.ub.cora.bookkeeper.data.converter;
	exports se.uu.ub.cora.bookkeeper.linkcollector;
	exports se.uu.ub.cora.bookkeeper.metadata;
	exports se.uu.ub.cora.bookkeeper.storage;
	exports se.uu.ub.cora.bookkeeper.termcollector;
	exports se.uu.ub.cora.bookkeeper.validator;
}