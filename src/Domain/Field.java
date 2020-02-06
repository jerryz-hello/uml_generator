package Domain;

public class Field extends Variable {

	public Visibility visibility;

	public Field(String name, String type, Visibility visibility) {
		super(name, type);
		this.visibility = visibility;
	}
}
