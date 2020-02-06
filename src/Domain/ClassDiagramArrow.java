package Domain;

public class ClassDiagramArrow extends AbstractArrow {

	public String cardinalityFrom;
	public String cardinalityTo;

	public ClassDiagramArrow(String type, String pointFrom, String pointTo) {
		super(type, pointFrom, pointTo);
		this.cardinalityTo = "";
		this.cardinalityFrom = "";
	}

	public ClassDiagramArrow(String type, String pointFrom, String pointTo, String numTo) {
		this(type, pointFrom, pointTo);
		this.cardinalityTo = numTo;
	}

	public ClassDiagramArrow(String type, String pointFrom, String pointTo, String numTo, String numFrom) {
		this(type, pointFrom, pointTo);
		this.cardinalityTo = numTo;
		this.cardinalityFrom = numFrom;
	}

	public boolean equals(ClassDiagramArrow in) {
		if (in.pointFrom.equals(this.pointFrom) && in.pointTo.equals(this.pointTo) && in.type.equals(this.type)) {
			return true;
		}
		return false;
	}

	public boolean isReverse(ClassDiagramArrow in) {
		if (this.pointFrom.equals(in.pointTo) && this.pointTo.equals(in.pointFrom) && this.type.equals(in.type)) {
			return true;
		}
		return false;
	}

}
