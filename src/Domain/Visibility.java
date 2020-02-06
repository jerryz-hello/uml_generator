package Domain;

public enum Visibility {
	DEFAULT("~"), PUBLIC("+"), PRIVATE("-"), PROTECTED("#");

	public String UMLString;

	private Visibility(String UMLString) {
		this.UMLString = UMLString;
	}

}
