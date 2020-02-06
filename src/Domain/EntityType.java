package Domain;

public enum EntityType {
	CLASS("class"), INTERFACE("interface"), ENUM("enum"), ABSTRACT("abstract class");

	public String UMLString;

	private EntityType(String UMLString) {
		this.UMLString = UMLString;
	}

}
