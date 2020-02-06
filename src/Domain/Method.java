package Domain;

import java.util.List;

public class Method {

	public String name;
	public String returnType;
	public Visibility visibility;
	public boolean isStatic;
	public List<Variable> args;

	public Method(String name, String returnType, Visibility visibility, boolean isStatic, List<Variable> args) {
		this.name = name;
		this.returnType = returnType;
		this.visibility = visibility;
		this.isStatic = isStatic;
		this.args = args;
	}

}
