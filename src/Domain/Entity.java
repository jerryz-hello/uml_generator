package Domain;

import java.util.List;

public class Entity {
	public String name;
	public EntityType classType;
	public Visibility visibility;
	public List<Method> methods;
	public List<Field> fields;

	public Entity(String name, EntityType classType, List<Method> methods, List<Field> fields, Visibility visibility) {
		this.name = name;
		this.classType = classType;
		this.methods = methods;
		this.fields = fields;
		this.visibility = visibility;
	}
}
