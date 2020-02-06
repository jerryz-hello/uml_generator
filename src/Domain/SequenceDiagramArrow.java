package Domain;

public class SequenceDiagramArrow extends AbstractArrow {
	
	public String tag;
	public boolean isCreate = false;

	public SequenceDiagramArrow(String type, String pointFrom, String pointTo, String tag, boolean isCreate) {
		super(type, pointFrom, pointTo);
		this.tag = tag;
		this.isCreate = isCreate;
	}

}
