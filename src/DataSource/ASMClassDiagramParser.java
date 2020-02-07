package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import Domain.ClassDiagramArrow;
import Domain.Entity;
import Domain.EntityType;
import Domain.Field;
import Domain.Method;
import Domain.Project;
import Domain.Variable;
import Domain.Visibility;

/**
 * Recursively parse through a list of classes. 
 * 
 * By calling its parse method with a list of classes to be parsed, for example:
 * <pre>{@code
 * parse({"java.lang.String", "java.util.ArrayList"})
 * }</pre> 
 * 
 * it will try to recursively discover their super classes, interfaces, and relationships,
 * and store these information in a Project wrapper class, which could be later sent to a IUMLClassDiagramGenerator
 * and rendered as a svg file
 * 
 * @author Jerry Zheng
 * @author Joseph Zou	
 * @see Presentation.IUMLClassDiagramGenerator
 * @since 1.0
 */
public class ASMClassDiagramParser implements IClassDiagramParser {

	private Queue<String> classNames;
	private List<String> visited;
	private List<Entity> entities;
	private List<ClassDiagramArrow> arrows;
	private ArrayList<Visibility> filters;
	private Set<String> supportedArrowTypes;
	private boolean parseExternal;
	
	/**
	 * Constructor 
	 * 
	 * @param visibility the level of visibility which decides what classes to be stored in the Project wrapper class
	 * @param supportedArrowTypes a set of supported arrow types, which might differ in different UML generators 
	 * @param parseExternal if set to true, it will attempt to parse classes that are used by but not belongs to the source code of the project
	 */
	public ASMClassDiagramParser(String visibility, Set<String> supportedArrowTypes, boolean parseExternal) {
		filters = new ArrayList<>(
				Arrays.asList(Visibility.PRIVATE, Visibility.PROTECTED, Visibility.PUBLIC, Visibility.DEFAULT));
		this.parseExternal = parseExternal;
		this.supportedArrowTypes = supportedArrowTypes;
		switch (visibility.toLowerCase()) {
		case "public":
			filters.remove(Visibility.PRIVATE);
			filters.remove(Visibility.PROTECTED);
			break;
		case "protected":
			filters.remove(Visibility.PRIVATE);
			break;
		default:
			break;
		}
	}

	/**
	 * Recursively discover the super class, interfaces, and relationships of the list of classes
	 * and store them in a Project wrapper class
	 * 
	 * @param args a list of String which contains the fully qualified names of java classes to be parsed
	 * @return Project 
	 * 
	 * @see Project
	 */
	@Override
	public Project parse(String[] args) throws IOException {

		this.entities = new ArrayList<>();
		this.arrows = new ArrayList<>();
		this.visited = new ArrayList<>();
		classNames = new LinkedList<>();
		ArrayList<IASMRelationshipParser> relationships = new ArrayList<>();

		for (String s : supportedArrowTypes) {
			switch (s) {
			case Constants.INHERITANCE:
				relationships.add(new ASMInheritanceParser(classNames, visited, arrows, parseExternal));
				break;
			case Constants.ASSOCIATION:
				relationships.add(new ASMAssociationParser(classNames, visited, arrows, parseExternal));
				break;
			case Constants.IMPLEMENTATION:
				relationships.add(new ASMImplementationParser(classNames, visited, arrows, parseExternal));
				break;
			case Constants.DEPENDENCY:
				relationships.add(new ASMDependencyParser(classNames, visited, arrows, parseExternal));
				break;
			}
		}

		classNames.addAll(Arrays.asList(args));

		while (!classNames.isEmpty()) {
			String name = classNames.remove().replace('/', '.');
			if (visited.contains(name)) {
				continue;
			}
			visited.add(name);
			if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
					|| name.startsWith(Constants.PLANTUML))) {
				continue;
			}
			ClassReader reader = new ClassReader(name);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES);
			Entity entity = entityBuilder(classNode);
			if (entity != null) {
				entities.add(entity);
			}
			for (IASMRelationshipParser p : relationships) {
				p.parse(classNode);
			}
		}

		List<ClassDiagramArrow> toRemove = new LinkedList<>();
		List<ClassDiagramArrow> toAdd = new LinkedList<>();
		for (int i = 0; i < arrows.size(); i++) {
			ClassDiagramArrow a = arrows.get(i);
			if (a.type.equals(Constants.DEPENDENCY) && a.pointFrom.equals(a.pointTo)) {
				toRemove.add(a);
			}
			for (int j = i + 1; j < arrows.size(); j++) {
				ClassDiagramArrow b = arrows.get(j);
				if (a.isReverse(b) && (a.type.equals(Constants.ASSOCIATION) || a.type.equals(Constants.DEPENDENCY))) {
					toAdd.add(new ClassDiagramArrow(Constants.DOUBLE + a.type, a.pointFrom, a.pointTo, b.cardinalityTo,
							a.cardinalityTo));
					toRemove.add(b);
					toRemove.add(a);
				}
			}
		}

		arrows.removeAll(toRemove);
		arrows.addAll(toAdd);

		return new Project(entities, new ArrayList<ClassDiagramArrow>(arrows));
	}

	private Entity entityBuilder(ClassNode classNode) throws IOException {
		String name = Type.getObjectType(classNode.name).getClassName().replace('/', '.');
		Visibility classVisibility = parseVisibility(classNode.access);
		if (!filters.contains(classVisibility)) {
			return null;
		}
		EntityType type = EntityType.CLASS;
		if ((classNode.access & Opcodes.ACC_INTERFACE) != 0) {
			type = EntityType.INTERFACE;
		}else if((classNode.access & Opcodes.ACC_ABSTRACT) != 0) {
			type = EntityType.ABSTRACT;
		}else if((classNode.access & Opcodes.ACC_ENUM) != 0) {
			type = EntityType.ENUM;
		}

		List<FieldNode> fields = (List<FieldNode>) classNode.fields;
		ArrayList<Field> classFields = new ArrayList<>();
		for (FieldNode field : fields) {
			String fieldName = field.name;
			String fieldType;
			if (field.signature == null) {
				fieldType = Type.getType(field.desc).getClassName();
			} else {
				fieldType = field.signature.replace('/', '.');
			}
			Visibility visibility = parseVisibility(field.access);
			if (!filters.contains(visibility)) {
				continue;
			}
			classFields.add(new Field(fieldName, fieldType, visibility));
		}

		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		ArrayList<Method> classMethods = new ArrayList<>();
		for (MethodNode method : methods) {
			String methodName = method.name;
			String returnType = Type.getReturnType(method.desc).getClassName();
			Visibility visibility = parseVisibility(method.access);
			if (!filters.contains(visibility)) {
				continue;
			}
			boolean isStatic = false;
			if ((method.access & Opcodes.ACC_STATIC) != 0) {
				isStatic = true;
			}

			ArrayList<Variable> arguments = new ArrayList<Variable>();
			// TODO: Access argument name
			String argName = "arg";
			int argCount = 0;
			for (Type argType : Type.getArgumentTypes(method.desc)) {
				String argumentType = argType.getClassName();
				arguments.add(new Variable(argName + argCount, argumentType));
				argCount++;
			}

			classMethods.add(new Method(methodName, returnType, visibility, isStatic, arguments));
		}
		return new Entity(name, type, classMethods, classFields, classVisibility);
	}

	private Visibility parseVisibility(int access) {
		Visibility visibility = Visibility.DEFAULT;
		if ((access & Opcodes.ACC_PRIVATE) != 0) {
			visibility = Visibility.PRIVATE;
		} else if ((access & Opcodes.ACC_PROTECTED) != 0) {
			visibility = Visibility.PROTECTED;
		} else if ((access & Opcodes.ACC_PUBLIC) != 0) {
			visibility = Visibility.PUBLIC;
		}
		return visibility;
	}

}
