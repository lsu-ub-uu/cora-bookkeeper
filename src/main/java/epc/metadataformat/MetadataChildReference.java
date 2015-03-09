package epc.metadataformat;

/**
 * MetadataChildReference is used to hold information about a child in metadata groups.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataChildReference {

	public static final int UNLIMITED = Integer.MAX_VALUE;

	private final String referenceId;
	private final int repeatMin;
	private final int repeatMax;

	public static MetadataChildReference withReferenceIdAndRepeatMinAndRepeatMax(String reference,
			int repeatMin, int repeatMax) {
		return new MetadataChildReference(reference, repeatMin, repeatMax);
	}

	private MetadataChildReference(String reference, int repeatMin, int repeatMax) {
		this.referenceId = reference;
		this.repeatMin = repeatMin;
		this.repeatMax = repeatMax;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public Object getRepeatMin() {
		return repeatMin;
	}

	public Object getRepeatMax() {
		return repeatMax;
	}

}
