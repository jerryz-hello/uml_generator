package Presentation;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataSource.Constants;
import Domain.Project;
import Domain.SequenceDiagramArrow;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLSequenceDiagramGenerator implements IUMLSequenceDiagramGenerator {

	static private Map<String, String> arrowTypes;
	static {
		arrowTypes = new HashMap<>();
		arrowTypes.put(Constants.CALL, "-%s>");
		arrowTypes.put(Constants.RETURN, "-%s->");
		arrowTypes.put(Constants.CREATE_SYMBOL, "**:");
		arrowTypes.put(Constants.NORMAL_SYMBOL, ":");
	}
	
	private OutputStream target;
	
	public PlantUMLSequenceDiagramGenerator(String fileName) {
		// TODO Auto-generated constructor stub
		try {
			this.target = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String generateScript(List<SequenceDiagramArrow> arrows) {
		String result = "@startuml\nautoactivate on\n";
		for(SequenceDiagramArrow a : arrows) {
			result += String.format("\"%s\" %s \"%s\" %s %s\n", a.pointFrom, String.format(arrowTypes.get(a.type), a.color.isEmpty() ? "" : "[" + a.color + "]"), a.pointTo, arrowTypes.get(a.isCreate ? Constants.CREATE_SYMBOL : Constants.NORMAL_SYMBOL), a.tag);
		}
		result += "@enduml";
		System.out.println(result);
		return result;
	}

	@Override
	public void generate(List<SequenceDiagramArrow> arrows) throws IOException {
		// TODO Auto-generated method stub
		String script = this.generateScript(arrows);
		SourceStringReader reader = new SourceStringReader(script);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		os.close();
		this.target.write(os.toByteArray());
	}

}
