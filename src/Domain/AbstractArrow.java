package Domain;

public abstract class AbstractArrow {

	public String pointFrom;
	public String pointTo;
	public String type;
	public String color = "";
	
	public AbstractArrow(String type, String pointFrom, String pointTo) {
		this.type = type;
		this.pointFrom = pointFrom;
		this.pointTo = pointTo;
		this.color = "";
	}
	
	public void assignColor(String color) {
		this.color = color;
	}


}
