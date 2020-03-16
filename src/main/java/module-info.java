module se.uu.ub.cora.bookkeeper {
	requires transitive se.uu.ub.cora.data;
	requires transitive se.uu.ub.cora.storage;

	exports se.uu.ub.cora.bookkeeper.linkcollector;
	exports se.uu.ub.cora.bookkeeper.metadata;
	exports se.uu.ub.cora.bookkeeper.termcollector;
	exports se.uu.ub.cora.bookkeeper.validator;
	exports se.uu.ub.cora.bookkeeper.recordpart;
}