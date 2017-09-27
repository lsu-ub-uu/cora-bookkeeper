package se.uu.ub.cora.bookkeeper.metadata;

public class CollectTerm {
	public final String type;
	public final String id;

	private CollectTerm(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public static CollectTerm createCollectTermWithTypeAndId(String type, String id) {
		return new CollectTerm(type, id);
	}
}
