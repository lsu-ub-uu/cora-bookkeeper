package epc.metadataformat;

/**
 * MetadataChildReference is used to hold information about a child in metadata
 * groups.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataChildReference {

	public static final int UNLIMITED = Integer.MAX_VALUE;
	private final String reference;
	private final int repeatMin;
	private final int repeatMax;

	public MetadataChildReference(String reference, int repeatMin, int repeatMax) {
		this.reference = reference;
		this.repeatMin = repeatMin;
		this.repeatMax = repeatMax;
	}

	public String getReference() {
		return reference;
	}

	public Object getRepeatMin() {
		return repeatMin;
	}

	public Object getRepeatMax() {
		return repeatMax;
	}

}
