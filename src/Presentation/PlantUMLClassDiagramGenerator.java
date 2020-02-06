package Presentation;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import DataSource.Constants;
import Domain.ClassDiagramArrow;
import Domain.Entity;
import Domain.EntityType;
import Domain.Field;
import Domain.Method;
import Domain.Project;
import Domain.Variable;
import Domain.Visibility;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLClassDiagramGenerator implements IUMLClassDiagramGenerator {

	static private Map<String, String> arrowTypes;
	static {
		arrowTypes = new HashMap<>();
		arrowTypes.put(Constants.INHERITANCE, "--|>");
		arrowTypes.put(Constants.IMPLEMENTATION, "..|>");
		arrowTypes.put(Constants.ASSOCIATION, "-->");
		arrowTypes.put(Constants.COMPOSITION, "--*");
		arrowTypes.put(Constants.AGGREGATION, "--o");
		arrowTypes.put(Constants.DEPENDENCY, "..>");
		arrowTypes.put(Constants.DOUBLE + Constants.ASSOCIATION, "<-->");
		arrowTypes.put(Constants.DOUBLE + Constants.DEPENDENCY, "<..>");
	}
	private OutputStream target;

	public PlantUMLClassDiagramGenerator(String fileName) {

		Visibility.PRIVATE.UMLString = "-";
		Visibility.PROTECTED.UMLString = "#";
		Visibility.PUBLIC.UMLString = "+";
		Visibility.DEFAULT.UMLString = "~";
		EntityType.CLASS.UMLString = "class";
		EntityType.INTERFACE.UMLString = "interface";
		EntityType.ENUM.UMLString = "enum";

		try {
			this.target = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String generateScript(Project project) {
		String results = "@startuml\n";
		for (Entity e : project.entities) {
			String content = "";
			for (Field f : e.fields) {
				content += String.format("%s%s \"%s\"\n", f.visibility.UMLString, f.type, f.name);
			}
			for (Method m : e.methods) {
				String argumentString = "";
				for (Variable a : m.args) {
					argumentString += String.format("%s %s, ", a.type, a.name);
				}
				if (argumentString.length() != 0) {
					argumentString = argumentString.substring(0, argumentString.length() - 2);
				}
				content += String.format("%s%s %s(%s)\n", m.visibility.UMLString, m.returnType, m.name, argumentString);
			}
			String entityString = String.format("%s%s %s {\n%s}\n", e.visibility.UMLString, e.classType.UMLString,
					e.name, content);
			results += entityString;
		}

		for (ClassDiagramArrow a : project.arrows) {
			if (a.cardinalityTo.equals(Constants.MANY)) {
				a.cardinalityTo = "*";
			}
			if (a.cardinalityFrom.equals(Constants.MANY)) {
				a.cardinalityFrom = "*";
			}
			String content;
			if (a.cardinalityTo.isEmpty()) {
				content = String.format("%s %s%s %s\n", a.pointFrom, arrowTypes.get(a.type), a.cardinalityTo,
						a.pointTo);
			} else {
				if (a.cardinalityFrom.isEmpty()) {
					content = String.format("%s %s \"%s\" %s\n", a.pointFrom, arrowTypes.get(a.type), a.cardinalityTo,
							a.pointTo);
				} else {
					content = String.format("%s \"%s\" %s \"%s\" %s\n", a.pointFrom, a.cardinalityFrom,
							arrowTypes.get(a.type), a.cardinalityTo, a.pointTo);
				}
			}
			results += content;
		}
		results += "@enduml";
		return results;
	}

	@Override
	public void generate(Project p) throws IOException {
		String script = this.generateScript(p);
		SourceStringReader reader = new SourceStringReader(script);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		os.close();
		this.target.write(os.toByteArray());
	}

}
